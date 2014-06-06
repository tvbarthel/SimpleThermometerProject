package fr.tvbarthel.apps.simplethermometer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;

import fr.tvbarthel.apps.simplethermometer.R;

public class PreferenceUtils {

	/*
        Shared Preference Keys
 	*/

    //Used to store the color of the background
    public static final String PREF_KEY_BACKGROUND_COLOR = "PrefKeyBackgroundColor";
    //Used to store the color of the text
    public static final String PREF_KEY_TEXT_COLOR = "PrefKeyTextColor";
    //Used to store the color of the foreground
    public static final String PREF_KEY_FOREGROUND_COLOR = "PrefKeyForegroundColor";
    //Used to store the last retrieved temperature (in Celsius)
    public static final String PREF_KEY_LAST_TEMPERATURE_IN_CELSIUS = "PrefKeylastTemperatureInCelsius";
    //Used to store the time of the last update (in Millis)
    public static final String PREF_KEY_LAST_UPDATE_TIME = "PrefKeyLastUpdateTime";
    //Used to store the temperature unit
    public static final String PREF_KEY_TEMPERATURE_UNIT_STRING = "PrefKeyTemperatureUnitString";
    //Used to store the opacity of the background
    public static final String PREF_KEY_BACKGROUND_OPACITY = "PrefKeyBackgroundOpacity";
    //Used to store the opacity of the foreground
    public static final String PREF_KEY_FOREGROUND_OPACITY = "PrefKeyForegroundOpacity";
    //Used to store the opacity of the text
    public static final String PREF_KEY_TEXT_OPACITY = "PrefKeyTextOpacity";

    private static final int DEFAULT_ALPHA = 255;


    /**
     * Return a human readable string that represents the current temperature stored
     * in {@code sharedPreferences}.
     *
     * @param context           the {@link android.content.Context} for getting the strings
     * @param sharedPreferences the {@link android.content.SharedPreferences} for retrieving the stored temperature
     * @return a {@link java.lang.String} representing the temperature.
     */
    public static String getTemperatureAsString(Context context, SharedPreferences sharedPreferences) {
        //Retrieve the unit symbol
        final String temperatureUnit = sharedPreferences.getString(PREF_KEY_TEMPERATURE_UNIT_STRING,
                context.getString(R.string.temperature_unit_celsius_symbol));

        //Retrieve the temperature
        Float temperatureFlt = sharedPreferences.getFloat(PREF_KEY_LAST_TEMPERATURE_IN_CELSIUS, 20);

        if (temperatureUnit.equals(context.getString(R.string.temperature_unit_fahrenheit_symbol))) {
            //Convert from Celsius to Fahrenheit
            temperatureFlt = temperatureFlt * 1.8f + 32f;
        } else if (temperatureUnit.equals(context.getString(R.string.temperature_unit_kelvin_symbol))) {
            //Convert from Celsius to Kelvin
            temperatureFlt += 273.15f;
        }
        //Format the temperature with only one decimal
        final String temperatureStr = new DecimalFormat("#.#").format(temperatureFlt);

        return temperatureStr + temperatureUnit;
    }

    public static int getTextAlpha(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(PREF_KEY_TEXT_OPACITY, DEFAULT_ALPHA);
    }

    public static int getBackgroundAlpha(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(PREF_KEY_BACKGROUND_OPACITY, DEFAULT_ALPHA);
    }

    public static int getForegroundAlpha(SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(PREF_KEY_FOREGROUND_OPACITY, DEFAULT_ALPHA);
    }

    /**
     * Return the text color stored in {@code sharedPreferences}
     *
     * @param context           the {@link android.content.Context} for getting the default value
     * @param sharedPreferences the {@link android.content.SharedPreferences} for retrieving the stored value
     * @return a color integer
     */
    public static int getTextColor(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(PREF_KEY_TEXT_COLOR,
                context.getResources().getColor(R.color.holo_blue));
    }

    /**
     * Return the background color stored in {@code sharedPreferences}
     *
     * @param context           the {@link android.content.Context} for getting the default value
     * @param sharedPreferences the {@link android.content.SharedPreferences} for retrieving the stored value
     * @return a color integer
     */
    public static int getBackgroundColor(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(PREF_KEY_BACKGROUND_COLOR,
                context.getResources().getColor(R.color.holo_blue));
    }

    /**
     * Return the foreground color stored in {@code sharedPreferences}
     *
     * @param context           the {@link android.content.Context} for getting the default value
     * @param sharedPreferences the {@link android.content.SharedPreferences} for retrieving the stored value
     * @return a color integer
     */
    public static int getForegroundColor(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(PreferenceUtils.PREF_KEY_FOREGROUND_COLOR,
                context.getResources().getColor(R.color.holo_blue_deep));
    }

    /**
     * Save {@code temperatureInCelsius} in {@code sharedPreferences}
     *
     * @param sharedPreferences    the {@link android.content.SharedPreferences} where the temperature is stored
     * @param temperatureInCelsius the temperature value in Celsius
     */
    public static void storeTemperatureInCelsius(SharedPreferences sharedPreferences, float temperatureInCelsius) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        //save the temperature value
        editor.putFloat(PreferenceUtils.PREF_KEY_LAST_TEMPERATURE_IN_CELSIUS, temperatureInCelsius);
        //save the time of the update
        editor.putLong(PreferenceUtils.PREF_KEY_LAST_UPDATE_TIME, System.currentTimeMillis());
        editor.commit();
    }
}
