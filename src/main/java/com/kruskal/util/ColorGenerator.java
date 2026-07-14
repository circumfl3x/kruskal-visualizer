package com.kruskal.util;

import javafx.scene.paint.Color;

public class ColorGenerator {
    private static final double GOLDEN_RATIO = 0.618033988749895;
    private static final double SATURATION = 0.7; // 0.0-1.0
    private static final double BRIGHTNESS = 0.9; // 0.0-1.0

    public static Color getColorForIndex(int index) {
        double hue = (index * GOLDEN_RATIO) % 1.0;
        return Color.hsb(hue * 360, SATURATION, BRIGHTNESS);
    }
}