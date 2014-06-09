package fr.tvbarthel.apps.simplethermometer.utils;


import android.graphics.Color;

/**
 * A simple utils class for colors.
 */
public final class ColorUtils {

    /**
     * Add some alpha to a color
     *
     * @param color the base color
     * @param alpha the alpha value
     * @return the new color
     */
    public static int addAlphaToColor(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    // Non-instantiable class.
    private ColorUtils() {
    }
}

