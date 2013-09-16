package fr.tvbarthel.apps.simplethermometer.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;

import fr.tvbarthel.apps.simplethermometer.TemperatureLoader;
import fr.tvbarthel.apps.simplethermometer.utils.ConnectivityUtils;

/**
 * An {@link android.appwidget.AppWidgetProvider} used to update the
 * Simple Thermometer Widgets
 */
public class STWidgetProvider extends AppWidgetProvider {

	//An intent Action used to notify a data change: text color, temperature value
	// temperature unit etc.
	public static final String APPWIDGET_DATA_CHANGED =
			"fr.vbarthel.apps.simplethermometer.widget.STWidgetProvider.DataChanged";

	/*
		AppWidgetProvider overrides
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		updateAppWidgets(context, appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		final String action = intent.getAction();
		boolean needAnUpdate = false;

		if (STWidgetProvider.APPWIDGET_DATA_CHANGED.equals(action)) {
			//if a data changed, an update is needed.
			needAnUpdate = true;
		} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action) && ConnectivityUtils.isNetworkConnected(context)) {
			//if the temperature is outdated and there is a network connection
			//an update is needed.
			needAnUpdate = true;
		}

		if (needAnUpdate) {
			//Retrieve the Simple Thermometer Widget Ids
			final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
			final ComponentName thisWidget = new ComponentName(context.getApplicationContext(), STWidgetProvider.class);
			final int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

			//Update the Simple Thermometer Widgets
			updateAppWidgets(context, allWidgetIds);
		}
	}

	/*
		Private Methods
	 */

	/**
	 * Update the Simple Thermometer Widgets by starting a {@link fr.tvbarthel.apps.simplethermometer.widget.STWidgetUpdateService}
	 *
	 * @param context      context
	 * @param appWidgetIds widget ids
	 */
	private void updateAppWidgets(Context context, int[] appWidgetIds) {
		//Check if the temperature is outdated
		boolean reloadTemperature = false;
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		if (TemperatureLoader.isTemperatureOutdated(defaultSharedPreferences,
				TemperatureLoader.UPDATE_INTERVAL_IN_MILLIS)) {
			reloadTemperature = true;
		}

		//Build an intent to start the update service
		final Intent intent = new Intent(context.getApplicationContext(), STWidgetUpdateService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		intent.putExtra(STWidgetUpdateService.EXTRA_RELOAD_TEMPERATURE, reloadTemperature);
		context.startService(intent);
	}

}
