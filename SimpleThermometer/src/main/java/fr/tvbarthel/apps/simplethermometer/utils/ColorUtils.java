package fr.tvbarthel.apps.simplethermometer.utils;


import android.graphics.Color;

public class ColorUtils {

    public static int addAlphaToColor(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
}
