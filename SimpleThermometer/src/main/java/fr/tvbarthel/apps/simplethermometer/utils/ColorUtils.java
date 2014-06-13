package fr.tvbarthel.apps.simplethermometer.utils;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import java.util.ArrayList;

import fr.tvbarthel.apps.simplethermometer.R;
import fr.tvbarthel.apps.simplethermometer.models.ColorPick;

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

    /**
     * Get the colors that can be picked.
     *
     * @param context a {@link android.content.Context} used to retrieve the resources.
     * @return an ArrayList of {@link fr.tvbarthel.apps.simplethermometer.models.ColorPick}
     */
    public static ArrayList<ColorPick> getColorPicks(Context context) {
        final ArrayList<ColorPick> colorPicks = new ArrayList<ColorPick>();
        final Resources resources = context.getResources();
        // Holo Blue
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_holo_blue),
                resources.getColor(R.color.holo_blue)));
        // Holo Blue Deep
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_holo_blue_deep),
                resources.getColor(R.color.holo_blue_deep)));
        // Holo Purple
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_holo_purple),
                resources.getColor(R.color.holo_purple)));
        // Holo Purple Deep
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_holo_purple_deep),
                resources.getColor(R.color.holo_purple_deep)));
        // Holo Green
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_holo_green),
                resources.getColor(R.color.holo_green)));
        // Holo Green Deep
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_holo_green_deep),
                resources.getColor(R.color.holo_green_deep)));
        // Holo Orange
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_holo_orange),
                resources.getColor(R.color.holo_orange)));
        // Holo Orange Deep
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_holo_orange_deep),
                resources.getColor(R.color.holo_orange_deep)));
        // Holo Red
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_holo_red),
                resources.getColor(R.color.holo_red)));
        // Holo Red Deep
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_holo_red_deep),
                resources.getColor(R.color.holo_red_deep)));
        // White
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_white),
                resources.getColor(R.color.white)));
        // Black
        colorPicks.add(new ColorPick(context.getString(R.string.color_name_black),
                resources.getColor(R.color.black)));

        return colorPicks;
    }


    // Non-instantiable class.
    private ColorUtils() {
    }
}

