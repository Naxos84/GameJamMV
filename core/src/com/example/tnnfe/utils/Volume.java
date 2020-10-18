package com.example.tnnfe.utils;

import com.badlogic.gdx.math.MathUtils;

public class Volume {

    /**
     * Returns a value between 0 and 1. The larger the distance between both values the smaller the volume
     */
    public static float of(float distance1, float distance2) {
        if (distance1 < distance2) {
            return MathUtils.clamp((distance1 / distance2 - 1) * -1, 0, 1);
        } else {
            return MathUtils.clamp((distance2 / distance1 - 1) * -1, 0, 1);
        }
    }
}
