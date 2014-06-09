package fr.tvbarthel.apps.simplethermometer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DecimalFormat;

import fr.tvbarthel.apps.simplethermometer.R;

/**
 * A simple utils class used to managed the different prefenreces within the application.
 */
public final class PreferenceUtils {

    public static final int PREF_ID_BACKGROUND = 100;
    public static final int PREF_ID_FOREGROUND = 200;
    public static final int PREF_ID_TEXT = 300;

    /**
     * A Simple enum that represents the different preferences that can be associated with an alpha value and a color.
     */
    public enum PreferenceId {
        BACKGROUND(PREF_KEY_BACKGROUND_COLOR, PREF_KEY_BACKGROUND_OPACITY, R.color.holo_blue),
        FOREGROUND(PREF_KEY_FOREGROUND_COLOR, PREF_KEY_FOREGROUND_OPACITY, R.color.holo_blue_deep),
        TEXT(PREF_KEY_TEXT_COLOR, PREF_KEY_TEXT_OPACITY, R.color.holo_blue);

        private String mKeyColor;
        private String mKeyAlpha;
        private int mDefaultColor;

        PreferenceId(String keyColor, String keyAlpha, int defaultColor) {
            mKeyAlpha = keyAlpha;
            mKeyColor = keyColor;
            mDefaultColor = defaultColor;
        }
    }

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
     * Get the default preferences associated with a {@link android.content.Context}.
     *
     * @param context the {@link android.content.Context} used to retrieve the {@link android.content.SharedPreferences}
     * @return the default {@link android.content.SharedPreferences}.
     */
    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    /**
     * Return a human readable string that represents the current temperature stored
     * in {@code sharedPreferences}.
     *
     * @param context the {@link android.content.Context} for getting the strings
     * @return a {@link java.lang.String} representing the temperature.
     */
    public static String getTemperatureAsString(Context context) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
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


    /**
     * Store the alpha value of a {@link fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils.PreferenceId}.
     *
     * @param context      the {@link android.content.Context} used to store the alpha value.
     * @param preferenceId the {@link fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils.PreferenceId} for which the alpha will be stored.
     * @param newValue     the new alpha value to store.
     * @return true if the new value was successfully written to persistence storage, false otherwise.
     */
    public static boolean storePreferedAlpha(Context context, PreferenceId preferenceId, int newValue) {
        final SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putInt(preferenceId.mKeyAlpha, newValue);
        return editor.commit();
    }

    /**
     * Get the alpha value associated with a {@link fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils.PreferenceId}.
     *
     * @param context      the {@link android.content.Context} used to get the alpha value.
     * @param preferenceId the {@link fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils.PreferenceId} associated with the alpha value.
     * @return the alpha value.
     */
    public static int getPreferedAlpha(Context context, PreferenceId preferenceId) {
        return getDefaultSharedPreferences(context).getInt(preferenceId.mKeyAlpha, DEFAULT_ALPHA);
    }

    /**
     * Store the color of a {@link fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils.PreferenceId}.
     *
     * @param context      the {@link android.content.Context} used to store the color.
     * @param preferenceId the {@link fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils.PreferenceId} that willl be associated with the color.
     * @param newValue     the new color value.
     * @return true if the value was successfully written to persistence storage, false otherwise.
     */
    public static boolean storePreferedColor(Context context, PreferenceId preferenceId, int newValue) {
        final SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putInt(preferenceId.mKeyColor, newValue);
        return editor.commit();
    }

    /**
     * Get the color associated with a {@link fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils.PreferenceId}.
     *
     * @param context      the {@link android.content.Context} used to get the color.
     * @param preferenceId the {@link fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils.PreferenceId} associated with the color.
     * @return the color.
     */
    public static int getPreferedColor(Context context, PreferenceId preferenceId) {
        return getDefaultSharedPreferences(context).getInt(preferenceId.mKeyColor, context.getResources().getColor(preferenceId.mDefaultColor));
    }

    // Non instantiable class.
    private PreferenceUtils() {
    }

}
