package com.example.tnnfe.controller;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.example.tnnfe.manager.EventManager;
import com.example.tnnfe.manager.events.Event;
import com.example.tnnfe.manager.events.EventListener;
import com.example.tnnfe.manager.events.EventType;
import com.example.tnnfe.model.*;
import com.example.tnnfe.utils.Globals;
import com.example.tnnfe.utils.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapController {

    private static final String OVER_LAYER = "Over";
    private static final String DUST_LAYER = "Dust";
    private static final String COLLISION_LAYER = "Collision";

    private final static int TIME_TILL_DUST_SPAWN = 30;

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;

    private int[] overLayerIndexes;
    private TiledMapTileLayer collisionLayer;

    private final EventManager eventManager;

    private Integer tileWidth;
    private Integer tileHeight;

    private TiledMapTileLayer dustLayer;
    private final HashMap<String, ArrayList<Dust>> mapNameRemovedDustMap;
    private ArrayList<Dust> currentRemovedDust;

    private final Map<String, List<Sign>> signs;
    private List<Sign> currentSigns;

    public List<Obstacle> obstacles;

    //private final List<Sign> signs;

    private float elapsedTimeSinceDustSpawn;

    public int getWidth() {

        MapProperties prop = tiledMap.getProperties();

        int mapWidth = prop.get("width", Integer.class);

        return mapWidth;
    }

    public int getHeight() {

        MapProperties prop = tiledMap.getProperties();

        int mapHeight = prop.get("height", Integer.class);

        return mapHeight;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public MapController(String filename, EventManager eventManager) {

        this.eventManager = eventManager;
        registerGameEvents(eventManager);

        mapNameRemovedDustMap = new HashMap<>();
        signs = new HashMap<>();

        loadMap(filename);
    }

    private void registerGameEvents(EventManager eventManager) {

        eventManager.register(EventType.PLAYER_MOVED, e -> checkWarps((Entity) e.getSource()));

        eventManager.register(EventType.PLAYER_MOVED, e -> checkRails((Entity) e.getSource()));

        eventManager.register(EventType.PLAYER_MOVED, new EventListener() {
            @Override
            public void executeEvent(Event e) {
                // hard events
                final Player player = (Player)e.getSource();

                if(player.getX() == 22 && player.getY() == 3) {
                    player.getCrystal().found();
                }

            }
        });

        eventManager.register(EventType.OBSTACLE_HIT, e -> handleObstacle((PlayerObstacleEventObject) e.getSource()));
    }

    private void loadMap(String filename) {

        if (tiledMap != null) {
            tiledMap.dispose();
        }

        tiledMap = new TmxMapLoader().load(filename);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Get indices
        ArrayList<Integer> overLayerIndexesList = new ArrayList<>();
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer.getName().equals(OVER_LAYER)) {
                overLayerIndexesList.add(tiledMap.getLayers().getIndex(layer));
            }
        }
        overLayerIndexes = new int[overLayerIndexesList.size()];
        for (int i = 0; i < overLayerIndexesList.size(); i++) {
            overLayerIndexes[i] = overLayerIndexesList.get(i);
        }

        collisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get(COLLISION_LAYER);

        // Set visibility off
        for (Integer layerIndex : overLayerIndexes) {
            tiledMap.getLayers().get(layerIndex).setVisible(false);
        }
        collisionLayer.setVisible(false);

        dustLayer = (TiledMapTileLayer) tiledMap.getLayers().get(DUST_LAYER);
        currentRemovedDust = mapNameRemovedDustMap.computeIfAbsent(filename, k -> new ArrayList<>());
        for (Dust removedDust : currentRemovedDust) {
            removeDust(removedDust.x, removedDust.y);
        }

        tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);

        // load signs
        loadSigns(filename);

        List<MapObject> mapObjects = getMapObjects("OBSTACLE");
        obstacles = createObstacleList(mapObjects);

        // Fire events
        eventManager.submit(new Event(tiledMap, EventType.MAP_LOADED));
    }

    /**
     * Checks the collision layer of the map and checks wether the cell given by the coordinates is occupied.
     *
     * @param x the x-coordinate of the cell to check
     * @param y the y-coordinate of the cell to check
     * @return true if Cell[x, y] is blocked within the collision layer
     */
    public boolean isBlocked(int x, int y) {

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);
        return cell != null;
    }

    public void collectDust(final Player player) {
        if (dustLayer != null) {
            int x = player.getX();
            int y = player.getY();
            TiledMapTileLayer.Cell cell = dustLayer.getCell(x, y);
            if (cell != null && cell.getTile() != null) {
                player.getCrystal().addDust(20);
                currentRemovedDust.add(new Dust(cell.getTile(), x, y));
                removeDust(x, y);
            }
        }
    }

    private void removeDust(int x, int y) {
        dustLayer.getCell(x, y).setTile(null);
    }

    private void loadSigns(final String filename) {

        List<MapObject> mapObjects = getMapObjects("Sign");

        List<Sign> signList = new ArrayList<>();

        for (MapObject mapObject : mapObjects) {
            float posX = mapObject.getProperties().get("x", Float.class);
            float posY = mapObject.getProperties().get("y", Float.class);
            float width = mapObject.getProperties().get("width", Float.class);
            float height = mapObject.getProperties().get("height", Float.class);
            String text = mapObject.getProperties().get("text", String.class);
            Boolean oneTimeBoolean = mapObject.getProperties().get("onetime", Boolean.class);
            boolean onetime = oneTimeBoolean != null && oneTimeBoolean;

            signList.add(new Sign(new Rectangle(posX, posY, width, height), text, onetime));
        }

        currentSigns = signs.computeIfAbsent(filename, s -> new ArrayList<>());

        if(currentSigns.isEmpty()){
            signList.forEach(sign -> currentSigns.add(sign));
        }
    }

    public boolean intersectsWithObstacle(Player player) {

        for (Obstacle obstacle : obstacles) {

            Direction direction = player.viewDirection;
            Vector2 relativeCoordinates = direction.getRelativeCoordinates();

            if (obstacle.getObstacleZone().contains(player.getX() + relativeCoordinates.x, player.getY() + relativeCoordinates.y)) {

                PlayerObstacleEventObject playerObstacleEventObject = new PlayerObstacleEventObject();
                playerObstacleEventObject.player = player;
                playerObstacleEventObject.obstacle = obstacle;

                eventManager.submit(new Event(playerObstacleEventObject, EventType.OBSTACLE_HIT));

                return true;

            }
        }

        return false;
    }

    public void update(float delta) {
        if (!currentRemovedDust.isEmpty()) {
            elapsedTimeSinceDustSpawn += delta;
        }

        if (elapsedTimeSinceDustSpawn > TIME_TILL_DUST_SPAWN) {
            int dustIndex = Globals.random.nextInt(currentRemovedDust.size());
            Dust dust = currentRemovedDust.remove(dustIndex);
            TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
            cell.setTile(dust.tile);
            dustLayer.setCell(dust.x, dust.y, cell);
            elapsedTimeSinceDustSpawn = 0;
        }
    }

    public void render(OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    public void renderOver(OrthographicCamera camera) {
        tiledMapRenderer.setView(camera);

        for (Integer layerIndex : overLayerIndexes) {
            tiledMap.getLayers().get(layerIndex).setVisible(true);
        }

        tiledMapRenderer.render(overLayerIndexes);
        for (Integer layerIndex : overLayerIndexes) {
            tiledMap.getLayers().get(layerIndex).setVisible(false);
        }
    }

    public void dispose() {
        tiledMap.dispose();
    }

    private List<MapObject> getMapObjects() {
        return getMapObjects(null);
    }

    public List<MapObject> getMapObjects(String type) {

        List<MapObject> objects = new ArrayList<>();

        for (MapLayer layer : tiledMap.getLayers()) {

            for (MapObject mapObject : layer.getObjects()) {

                // Filter by type
                if (Strings.isNotNullOrEmpty(type)) {

                    if (mapObject.getProperties().containsKey("type")) {

                        String typeFromObject = mapObject.getProperties().get("type", String.class);

                        if (type.equals(typeFromObject)) {
                            objects.add(mapObject);
                        }
                    }
                }
            }
        }

        return objects;
    }

    public static List<MapObject> getMapObjectsFromMap(TiledMap tiledMap, String type) {
        List<MapObject> objects = new ArrayList<>();

        for (MapLayer layer : tiledMap.getLayers()) {

            for (MapObject mapObject : layer.getObjects()) {

                // Filter by type
                if (Strings.isNotNullOrEmpty(type)) {

                    if (mapObject.getProperties().containsKey("type")) {

                        String typeFromObject = mapObject.getProperties().get("type", String.class);

                        if (type.equals(typeFromObject)) {
                            objects.add(mapObject);
                        }
                    }
                }
            }
        }

        return objects;
    }

    private void handleObstacle(final PlayerObstacleEventObject playerObstacleEventObject) {

        boolean enoughBees = playerObstacleEventObject.player.getBees() >= playerObstacleEventObject.obstacle.getNeededBees();

        String[] texts = getObstacleText(playerObstacleEventObject.obstacle, enoughBees);

        this.obstacles.remove(playerObstacleEventObject.obstacle);

        eventManager.submit(new Event(texts, EventType.SHOW_DIALOG));

    }

    private String[] getObstacleText(Obstacle obstacle, boolean enoughBees) {
        String[] texts = new String[2];
        texts[0] = obstacle.getPreText();

        //if (player.getBees() >= obstacle.getNeededBees()) {
        if (enoughBees) {

            texts[1] = obstacle.getOkayText();

        } else {

            texts[1] = "Das hier ist ein toller Text.";

        }
        return texts;
    }

    private List<Obstacle> createObstacleList(List<MapObject> mapObjects) {

        List<Obstacle> obstacles = new ArrayList<>();

        for (MapObject mapObject : mapObjects) {
            Obstacle obstacle = createObstacle(mapObject);
            obstacles.add(obstacle);
        }

        return obstacles;
    }

    private Obstacle createObstacle(MapObject mapObject) {

        final float CONTAINS_CHECK_OFFSET = 0.01f;

        Integer neededBess = mapObject.getProperties().get("NEEDED_BEES", Integer.class);
        String pretext = mapObject.getProperties().get("TEXT_PRE", String.class);
        String okayText = mapObject.getProperties().get("TEXT_OK", String.class);
        Float posX = mapObject.getProperties().get("x", Float.class) / tileWidth;
        Float posY = mapObject.getProperties().get("y", Float.class) / tileHeight - CONTAINS_CHECK_OFFSET;
        Float width = mapObject.getProperties().get("width", Float.class) / tileWidth - CONTAINS_CHECK_OFFSET;
        Float height = mapObject.getProperties().get("height", Float.class) / tileHeight;

        return new Obstacle(posX, posY, width, height, neededBess, pretext, okayText);
    }

    private void checkRails(final Entity player) {

        // rail hit

        List<Rail> rails = new ArrayList<>();
        List<MapObject> railMapObjects = getMapObjects("RAIL");
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), 1, 1);
        for (MapObject railObject : railMapObjects) {

            Float posX = railObject.getProperties().get("x", Float.class) / tileWidth;
            Float posY = railObject.getProperties().get("y", Float.class) / tileHeight;
            Float width = railObject.getProperties().get("width", Float.class) / tileWidth;
            Float height = railObject.getProperties().get("height", Float.class) / tileHeight;
            Rectangle railRect = new Rectangle(posX, posY, width, height);

            if (!Intersector.overlaps(playerRect, railRect)) {
                continue;
            }

            Map<Integer, List<Direction>> railDirectionProperties = new HashMap<>();

            MapProperties properties = railObject.getProperties();

            for (Iterator<String> it = properties.getKeys(); it.hasNext(); ) {
                final String key = it.next();
                if (key.contains("STEP-")) {
                    // map key
                    String stepIndex = key.substring(5);

                    String value = (String) properties.get(key);
                    String[] split = value.split(":");

                    // 0 -> move type
                    Direction direction = null;
                    switch (split[0]) {
                        case "MOVE_DOWN": {
                            direction = Direction.DOWN;
                            break;
                        }
                        case "MOVE_UP": {
                            direction = Direction.UP;
                            break;
                        }
                        case "MOVE_LEFT": {
                            direction = Direction.LEFT;
                            break;
                        }
                        case "MOVE_RIGHT": {
                            direction = Direction.RIGHT;
                            break;
                        }
                    }

                    List<Direction> directions = new ArrayList<>();
                    for (int i = 0; i < Integer.valueOf(split[1]); i++) {
                        directions.add(direction);
                    }

                    railDirectionProperties.put(Integer.valueOf(stepIndex), directions);

                }
            }

            List<Direction> movementList = new ArrayList<>();
            for (int i = 0; i < railDirectionProperties.size(); i++) {
                List<Direction> directions = railDirectionProperties.get(i);
                directions.forEach(movementList::add);
            }


            rails.add(new Rail(movementList));
            eventManager.submit(new Event(rails, EventType.RAIL_HIT));
        }
    }

    private void checkWarps(final Entity player) {

        List<Warp> warps = new ArrayList<>();

        List<MapObject> mapObjects = getMapObjects("WARP");

        for (MapObject mapObject : mapObjects) {

            String map = mapObject.getProperties().get("DEST_MAP", String.class);
            Float posX = mapObject.getProperties().get("x", Float.class);
            Float posY = mapObject.getProperties().get("y", Float.class);
            Float width = mapObject.getProperties().get("width", Float.class);
            Float height = mapObject.getProperties().get("height", Float.class);
            Integer destX = mapObject.getProperties().get("DEST_X", Integer.class);
            Integer destY = mapObject.getProperties().get("DEST_Y", Integer.class);

            warps.add(new Warp(posX / tileWidth,
                    posY / tileHeight,
                    width / tileWidth,
                    height / tileHeight,
                    map,
                    destX,
                    destY));
        }

        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), 1, 1);
        // Gdx.app.debug("playerRect", player.getX() + ":" + player.getY() + " - " + player.getWidth() + ":" + player.getHeight());

        for (Warp warp : warps) {

            if (Intersector.overlaps(playerRect, warp.warpZone)) {

                loadMap("maps/" + warp.map + ".tmx");

                Integer mapWidth = tiledMap.getProperties().get("width", Integer.class);
                Integer mapHeight = tiledMap.getProperties().get("height", Integer.class);

                int destX = warp.destX;
                int destY = mapHeight - warp.destY - 1;

                player.setX(destX);
                player.setY(destY);
            }
        }
    }

    public String[] getDialogTexts(final int x, final int y) {
        Rectangle playerRect = new Rectangle(x * tileWidth, y * tileHeight, tileWidth, tileHeight);

        for (Sign sign : currentSigns) {
            if (Intersector.overlaps(sign.rect, playerRect) && (!sign.isOnetime || !sign.wasPassed)) {
                sign.wasPassed = true;
                return sign.texts;
            }
        }

        return new String[0];
    }

    private class Warp {

        private Rectangle warpZone;

        private String map;

        private Integer destX;
        private Integer destY;

        Warp(float x, float y, float width, float height, String map, Integer destX, Integer destY) {

            this.warpZone = new Rectangle(x, y, width, height);

            this.map = map;
            this.destX = destX;
            this.destY = destY;
        }
    }

    private class Sign {

        private final Rectangle rect;

        private final String[] texts;

        private final boolean isOnetime;

        private boolean wasPassed;

        private Sign(Rectangle rect, String text, boolean isOnetime) {
            this.rect = rect;
            this.texts = text.split("#");
            this.isOnetime = isOnetime;
        }
    }

    private class Dust {

        private final TiledMapTile tile;

        private final int x, y;

        Dust(TiledMapTile tile, int x, int y) {
            this.tile = tile;
            this.x = x;
            this.y = y;
        }
    }
}