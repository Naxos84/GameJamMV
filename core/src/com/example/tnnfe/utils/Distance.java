package com.example.tnnfe.utils;

public class Distance {

    /**
     * Calculates the distance between 2 points
     */
    public static float between(float x1, float y1, float x2, float y2) {
        float a = x1 - x2;
        float b = y1 - y2;

        double aSquared = Math.pow(a, 2);
        double bSquared = Math.pow(b, 2);

        double c = Math.sqrt(aSquared + bSquared);

        return (float) c;
    }

}
