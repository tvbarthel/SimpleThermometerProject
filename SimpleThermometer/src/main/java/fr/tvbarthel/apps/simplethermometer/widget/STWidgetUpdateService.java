package fr.tvbarthel.apps.simplethermometer.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import fr.tvbarthel.apps.simplethermometer.MainActivity;
import fr.tvbarthel.apps.simplethermometer.R;
import fr.tvbarthel.apps.simplethermometer.TemperatureLoader;
import fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils;

/**
 * A {@link android.app.Service} used by the {@link fr.tvbarthel.apps.simplethermometer.widget.STWidgetProvider}
 * to update the Simple Thermometer Widgets.
 */
public class STWidgetUpdateService extends Service implements TemperatureLoader.Listener {

	public static final String EXTRA_RELOAD_TEMPERATURE = "ExtraReloadTemperature";

	//The instance of the AppWidgetManager
	private AppWidgetManager mAppWidgetManager;
	//The Simple Thermometer Widget Ids
	private int[] mAllWidgetIds;

	/*
		Service overrides
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mAppWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
		mAllWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		//Check if the temperature has to be reloaded
		if (intent.getBooleanExtra(EXTRA_RELOAD_TEMPERATURE, false)) {
			//start a temperature loader
			new TemperatureLoader(this, getApplicationContext()).start();
		} else {
			//finalize the update
			finalizeUpdateRequest();
		}

		//Try to avoid a null intent, got a NullPointerException in a report crash. Dunno the reason :s
		return START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*
		Private Methods
	 */

	/**
	 * Update the app widgets according to the stored parameters.
	 */
	private void updateAppWidgets() {
		//Retrieve the stored values
		final Context context = getApplicationContext();
		final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		final String temperature = PreferenceUtils.getTemperatureAsString(context, defaultSharedPreferences);
		final int textColor = PreferenceUtils.getTextColor(context, defaultSharedPreferences);
		final int backgroundColor = PreferenceUtils.getBackgroundColor(context, defaultSharedPreferences);
		final int foregroundColor = PreferenceUtils.getForegroundColor(context, defaultSharedPreferences);

		//Update all the app widgets
		for (int widgetId : mAllWidgetIds) {
			final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

			//Use the stored values to update the app widget
			remoteViews.setTextViewText(R.id.widget_temperature, temperature);
			remoteViews.setTextColor(R.id.widget_temperature, textColor);
			remoteViews.setInt(R.id.widget_root_layout, "setBackgroundColor", backgroundColor);
			remoteViews.setInt(R.id.widget_fair_icon, "setColorFilter", foregroundColor);
			remoteViews.setInt(R.id.widget_storm_icon, "setColorFilter", foregroundColor);

			//Add a clickIntent on the app widget
			//This Intent will launch the SimpleThermometer Application
			final Intent clickIntent = new Intent(getApplicationContext(), MainActivity.class);
			final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent);

			//Update the app widget
			mAppWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	/**
	 * Finalize the Update Request
	 */
	private void finalizeUpdateRequest() {
		updateAppWidgets();
		stopSelf();
	}

	/*
		TemperatureLoader.Listener Overrides
	 */

	@Override
	public void onTemperatureLoadingSuccess() {
		finalizeUpdateRequest();
	}

	@Override
	public void onTemperatureLoadingProgress(int progress) {
		finalizeUpdateRequest();
	}

	@Override
	public void onTemperatureLoadingFail(int stringResourceId) {
		finalizeUpdateRequest();
	}

	@Override
	public void onTemperatureLoadingCancelled() {
		finalizeUpdateRequest();
	}
}
