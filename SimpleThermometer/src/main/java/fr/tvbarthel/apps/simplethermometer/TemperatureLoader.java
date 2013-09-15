package fr.tvbarthel.apps.simplethermometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserAsyncTask;
import fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult;
import fr.tvbarthel.apps.simplethermometer.preferences.PreferenceUtils;

public class TemperatureLoader extends OpenWeatherMapParserAsyncTask{

	private Context mContext;

	public TemperatureLoader(Listener listener, Context context) {
		super(listener);
		mContext = context;
	}

	@Override
	protected void onPostExecute(OpenWeatherMapParserResult result) {
		super.onPostExecute(result);
		if (result!= null && result.getTemperatureValue() != null) {
			//Store the temperature and the time of the update
			//in the default shared preferences
			final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
			PreferenceUtils.storeTemperatureInCelsius(defaultSharedPreferences, result.getTemperatureValue());
		}
	}
}
