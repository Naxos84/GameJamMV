package com.example.tnnfe.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.tnnfe.commands.Command;
import com.example.tnnfe.commands.MoveDownCommand;
import com.example.tnnfe.commands.MoveLeftCommand;
import com.example.tnnfe.commands.MoveRightCommand;
import com.example.tnnfe.commands.MoveUpCommand;
import com.example.tnnfe.manager.EventManager;
import com.example.tnnfe.manager.events.Event;
import com.example.tnnfe.manager.events.EventListener;
import com.example.tnnfe.manager.events.EventType;
import com.example.tnnfe.model.Direction;
import com.example.tnnfe.model.Player;
import com.example.tnnfe.model.Rail;
import com.example.tnnfe.utils.Globals;
import com.example.tnnfe.view.PlayerView;

import java.util.List;

public class PlayerController implements InputProcessor {

    Command moveUpCommand;
    Command moveRightCommand;
    Command moveDownCommand;
    Command moveLeftCommand;

    Player player;

    PlayerView playerView;

    MapController mapController;

    EventManager eventManager;

    private float cooldown = 0;

    boolean movementDisabled = false;
    boolean railInProcess = false;
    List<Direction> railCommands;
    int currentRailCommand;
    float minimumDeltaTime = 0.20f;
    float currentDeltaTime = 0;

    private String easterEgg = "";

    public PlayerController(int x, int y, Texture textureRegion, MapController mapController, EventManager eventManager) {

        player = new Player(x, y, Globals.CELL_SIZE, Globals.CELL_SIZE);
        playerView = new PlayerView(textureRegion);

        this.mapController = mapController;
        moveUpCommand = new MoveUpCommand();
        moveRightCommand = new MoveRightCommand();
        moveDownCommand = new MoveDownCommand();
        moveLeftCommand = new MoveLeftCommand();

        this.eventManager = eventManager;
        registerGameEvents(eventManager);
    }

    private boolean dialogIsShowed = false;

    private void registerGameEvents(EventManager eventManager) {

        eventManager.register(EventType.RAIL_HIT, e -> executeRail((List<Rail>) e.getSource()));

        eventManager.register(EventType.SHOW_DIALOG, new EventListener() {
            @Override
            public void executeEvent(Event e) {
                dialogIsShowed = true;
                player.isMoving = false;
            }
        });

        eventManager.register(EventType.DIALOG_CLOSED, new EventListener() {
            @Override
            public void executeEvent(Event e) {
                dialogIsShowed = false;
                player.isMoving = true;
            }
        });
        eventManager.register(EventType.BEE_COLLECTED, new EventListener() {
            @Override
            public void executeEvent(Event e) {
                int collectedBees = (int)e.getSource();
                player.addBees(collectedBees);
                System.out.println("Player has now " + player.getBees());
            }
        });
    }

    private void executeRail(List<Rail> rails) {

        if (railInProcess) {
            return;
        }
        currentRailCommand = 0;
        movementDisabled = true;
        railInProcess = true;

        railCommands = rails.get(0).getCommands();
    }

    public int getPlayerPositionX() {
        return player.getX();
    }

    public int getPlayerPositionY() {
        return player.getY();
    }

    private boolean isKeyPressed(int keyCode) {
        return Gdx.input.isKeyPressed(keyCode);
    }

    public void update(final float deltaTime) {
        if (!movementDisabled) {
            cooldown -= deltaTime;
            player.isMoving = isKeyPressed(Input.Keys.UP)
                    || isKeyPressed(Input.Keys.W)
                    || isKeyPressed(Input.Keys.RIGHT)
                    || isKeyPressed(Input.Keys.D)
                    || isKeyPressed(Input.Keys.DOWN)
                    || isKeyPressed(Input.Keys.S)
                    || isKeyPressed(Input.Keys.LEFT)
                    || isKeyPressed(Input.Keys.A)
                    || railInProcess;

            mapController.collectDust(player);

            if (cooldown < 0 && !player.isReading()) {
                cooldown = 0;
                Command command = handleInput();
                if (command != null) {
                    command.execute(player);
                    eventManager.submit(new Event(player, EventType.PLAYER_MOVED));
                }

                cooldown += 0.15;
            }
        } else {
            if (!railInProcess) {
                player.isMoving = false;
            }
        }

        currentDeltaTime += deltaTime;

        if (currentDeltaTime > minimumDeltaTime) {
            currentDeltaTime = 0;

            String[] dialogTexts = mapController.getDialogTexts(player.getX(), player.getY());
            if (dialogTexts.length > 0) {
                eventManager.submit(new Event(dialogTexts, EventType.SHOW_DIALOG));
            }

            if (railInProcess && !dialogIsShowed) {

                Direction direction = railCommands.get(currentRailCommand);

                switch (direction) {
                    case UP: {

                        Gdx.app.debug("", "UP");
                        player.viewDirection = Direction.UP;
                        moveUpCommand.execute(player);
                        break;
                    }
                    case RIGHT: {
                        Gdx.app.debug("", "RIGHT");
                        player.viewDirection = Direction.RIGHT;
                        moveRightCommand.execute(player);
                        break;
                    }
                    case DOWN: {
                        Gdx.app.debug("", "DOWN");
                        player.viewDirection = Direction.DOWN;
                        moveDownCommand.execute(player);
                        break;
                    }
                    case LEFT: {
                        Gdx.app.debug("", "LEFT");
                        player.viewDirection = Direction.LEFT;
                        moveLeftCommand.execute(player);
                        break;
                    }
                }

                eventManager.submit(new Event(player, EventType.PLAYER_MOVED));
                currentRailCommand++;

                if (currentRailCommand == railCommands.size()) {
                    railInProcess = false;
                    movementDisabled = false;
                    currentRailCommand = 0;
                }
            }
        }
        // Crystal logic - found, activation, update
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.getCrystal().switchOnOff();
        } /*else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            player.getCrystal().found();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            player.getCrystal().addDust(10);
            Gdx.app.debug("", "" + player.getCrystal().getDust());
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            player.addBees(10);
        }*/
        player.getCrystal().update(deltaTime);

        playerView.update(Gdx.graphics.getDeltaTime(), player);
    }

    public void firePlayerMovedEvent() {
        eventManager.submit(new Event(player, EventType.PLAYER_MOVED));
    }

    private Command handleInput() {

        if (isKeyPressed(Input.Keys.UP) || isKeyPressed(Input.Keys.W)) {
            player.viewDirection = Direction.UP;
            if (canPlayerMoveUp()) {
                return moveUpCommand;
            }
        } else if (isKeyPressed(Input.Keys.RIGHT) || isKeyPressed(Input.Keys.D)) {
            player.viewDirection = Direction.RIGHT;
            if (canPlayerMoveRight()) {
                return moveRightCommand;
            }
        } else if (isKeyPressed(Input.Keys.DOWN) || isKeyPressed(Input.Keys.S)) {
            player.viewDirection = Direction.DOWN;
            if (canPlayerMoveDown()) {
                return moveDownCommand;
            }
        } else if (isKeyPressed(Input.Keys.LEFT) || isKeyPressed(Input.Keys.A)) {
            player.viewDirection = Direction.LEFT;
            if (canPlayerMoveLeft()) {
                return moveLeftCommand;
            }
        }

        return null;
    }

    public void render(SpriteBatch spriteBatch) {
        playerView.render(spriteBatch, player);
    }

    public void renderCrystal(SpriteBatch spriteBatch, float cameraX, float cameraY, int width, int height) {
        playerView.renderCrystal(spriteBatch, player, cameraX, cameraY, width, height);
    }

    Texture overlayTexture;

    public void renderOverlay(SpriteBatch spriteBatch) {
        if(overlayTexture != null) {
            overlayTexture.dispose();
        }
        Pixmap grayOverlay = new Pixmap(Globals.CELL_SIZE * mapController.getWidth(), Globals.CELL_SIZE * mapController.getWidth(), Pixmap.Format.RGBA8888);
        grayOverlay.setColor(new Color(0.383f, 0.324f, 0.13f, 1 - Math.min(1, 0.67f + player.getBees() / 500f)));
        grayOverlay.fillRectangle(0, 0, Globals.CELL_SIZE * 100, Globals.CELL_SIZE * 100);

        overlayTexture = new Texture(grayOverlay);
        spriteBatch.draw(overlayTexture, 0, 0);
        grayOverlay.dispose();
    }

    public Player getPlayer() {
        return player;
    }

    private boolean canPlayerMoveUp() {
        return !mapController.isBlocked(player.getX(), player.getY() + 1)
                && !mapController.intersectsWithObstacle(player);
    }

    private boolean canPlayerMoveRight() {

        return !mapController.isBlocked(player.getX() + 1, player.getY())
                && !mapController.intersectsWithObstacle(player);
    }

    private boolean canPlayerMoveDown() {
        return !mapController.isBlocked(player.getX(), player.getY() - 1)
                && !mapController.intersectsWithObstacle(player);
    }

    private boolean canPlayerMoveLeft() {
        return !mapController.isBlocked(player.getX() - 1, player.getY())
                && !mapController.intersectsWithObstacle(player);
    }

    @Override
    public boolean keyDown(final int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(final int keycode) {
        if (keycode == Input.Keys.ENTER) {
            String[] dialogTexts = mapController.getDialogTexts(player.getX(), player.getY());
            if (dialogTexts.length > 0) {
                eventManager.submit(new Event(dialogTexts, EventType.SHOW_DIALOG));
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(final char character) {
        return false;
    }

    @Override
    public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
        return false;
    }

    @Override
    public boolean touchDragged(final int screenX, final int screenY, final int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(final int screenX, final int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(final int amount) {
        return false;
    }
}
