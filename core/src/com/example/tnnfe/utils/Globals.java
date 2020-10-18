package com.example.tnnfe.utils;

import java.util.Random;

public class Globals {

    public static final int CELL_SIZE = 16;
    public static final int CELLS_TILL_SILENCE = 20;
    public static final float CELL_PANING = CELLS_TILL_SILENCE * 0.6f;

    public static final Random random = new Random();
}
