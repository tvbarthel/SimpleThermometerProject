package fr.tvbarthel.apps.simplethermometer.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import fr.tvbarthel.apps.simplethermometer.MainActivity;
import fr.tvbarthel.apps.simplethermometer.R;
import fr.tvbarthel.apps.simplethermometer.TemperatureLoader;
import fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserAsyncTask;
import fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult;
import fr.tvbarthel.apps.simplethermometer.preferences.PreferenceUtils;

public class STWidgetUpdateService extends Service implements OpenWeatherMapParserAsyncTask.Listener {

	public static final String EXTRA_RELOAD_TEMPERATURE = "ExtraReloadTemperature";

	private AppWidgetManager mAppWidgetManager;
	private int[] mAlWidgetIds;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		mAppWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
		mAlWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		if (intent.getBooleanExtra(EXTRA_RELOAD_TEMPERATURE, false)) {
			reloadTemperature();
		} else {
			finalizeUpdate();
		}

		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void updateAppWidgets() {
		final Context context = getApplicationContext();
		final SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		final String temperature = PreferenceUtils.getTemperatureAsString(context, defaultSharedPreferences);
		final int textColor = PreferenceUtils.getTextColor(context, defaultSharedPreferences);
		final int backgroundColor = PreferenceUtils.getBackgroundColor(context, defaultSharedPreferences);

		for (int widgetId : mAlWidgetIds) {
			final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
			remoteViews.setTextViewText(R.id.widget_temperature, temperature);
			remoteViews.setTextColor(R.id.widget_temperature, textColor);
			remoteViews.setInt(R.id.widget_frame_layout, "setBackgroundColor", backgroundColor);

			final Intent clickIntent = new Intent(getApplicationContext(), MainActivity.class);
			final PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.widget_frame_layout, pendingIntent);

			mAppWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	private void reloadTemperature() {
		//retrieve an instance of the LocationManager
		final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		//Get a location with a coarse accuracy
		final Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		final String provider = locationManager.getBestProvider(criteria, true);
		if (provider == null) return;
		final Location location = locationManager.getLastKnownLocation(provider);

		if (location != null) {
			//Retrieve the latitude and the longitude and execute a weather loader
			final double latitude = location.getLatitude();
			final double longitude = location.getLongitude();
			new TemperatureLoader(this, getApplicationContext()).execute(
					String.format(getResources().getString(R.string.url_open_weather_api), latitude, longitude));
		} else {
			finalizeUpdate();
		}
	}

	private void finalizeUpdate() {
		updateAppWidgets();
		stopSelf();
	}


	@Override
	public void onWeatherLoadingSuccess(OpenWeatherMapParserResult result) {
		finalizeUpdate();
	}

	@Override
	public void onWeatherLoadingFail(int stringResourceId) {
		finalizeUpdate();
	}

	@Override
	public void onWeatherLoadingProgress(int progress) {
	}

	@Override
	public void onWeatherLoadingCancelled() {
		finalizeUpdate();
	}
}
