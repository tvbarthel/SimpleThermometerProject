package fr.tvbarthel.apps.simplethermometer.preferences;

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
	//Used to store the color of the icons
	public static final String PREF_KEY_ICON_COLOR = "PrefKeyIconColor";
	//Used to store the last retrieved temperature (in Celsius)
	public static final String PREF_KEY_LAST_TEMPERATURE_IN_CELSIUS = "PrefKeylastTemperatureInCelsius";
	//Used to store the time of the last update (in Millis)
	public static final String PREF_KEY_LAST_UPDATE_TIME = "PrefKeyLastUpdateTime";
	//Used to store the temperature unit
	public static final String PREF_KEY_TEMPERATURE_UNIT_STRING = "PrefKeyTemperatureUnitString";


	public static String getTemperatureAsString(Context context, SharedPreferences sharedPreferences) {
		final String temperatureUnit = sharedPreferences.getString(PREF_KEY_TEMPERATURE_UNIT_STRING,
				context.getString(R.string.temperature_unit_celsius_symbol));
		//Retrieve the unit symbol
		Float temperatureFlt = sharedPreferences.getFloat(PREF_KEY_LAST_TEMPERATURE_IN_CELSIUS, 20);

		if (temperatureUnit.equals(context.getString(R.string.temperature_unit_fahrenheit_symbol))) {
			//Convert from Celsius to Fahrenheit
			temperatureFlt += 32f;
		} else if (temperatureUnit.equals(context.getString(R.string.temperature_unit_kelvin_symbol))) {
			//Convert from Celsius to Kelvin
			temperatureFlt += 273.15f;
		}
		//Format the temperature with only one decimal
		final String temperatureStr = new DecimalFormat("#.#").format(temperatureFlt);

		return temperatureStr + temperatureUnit;
	}

	public static int getTextColor(Context context, SharedPreferences sharedPreferences) {
		return sharedPreferences.getInt(PREF_KEY_TEXT_COLOR,
				context.getResources().getColor(R.color.black));
	}

	public static int getBackgroundColor(Context context, SharedPreferences sharedPreferences) {
		return sharedPreferences.getInt(PREF_KEY_BACKGROUND_COLOR,
				context.getResources().getColor(R.color.holo_blue));
	}

	public static int getIconColor(Context context, SharedPreferences sharedPreferences) {
		return sharedPreferences.getInt(PreferenceUtils.PREF_KEY_ICON_COLOR,
				context.getResources().getColor(R.color.white));
	}

	public static void storeTemperatureInCelsius(SharedPreferences sharedPreferences, float temperatureInCelsius) {
		final SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putFloat(PreferenceUtils.PREF_KEY_LAST_TEMPERATURE_IN_CELSIUS, temperatureInCelsius);
		editor.putLong(PreferenceUtils.PREF_KEY_LAST_UPDATE_TIME, System.currentTimeMillis());
		editor.commit();
	}
}
