package fr.tvbarthel.apps.simplethermometer.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import fr.tvbarthel.apps.simplethermometer.R;
import fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParser;
import fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult;
import fr.tvbarthel.apps.simplethermometer.utils.ConnectivityUtils;
import fr.tvbarthel.apps.simplethermometer.utils.LocationUtils;
import fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils;
import fr.tvbarthel.apps.simplethermometer.widget.STWidgetProvider;

/**
 * A simple {@link android.app.IntentService} that updates the temperature.
 */
public class TemperatureUpdaterService extends Service implements LocationListener {

    // update action
    public static final String ACTION_UPDATE = "TemperatureUpdaterService.Actions.Update";
    // update error action
    public static final String ACTION_UPDATE_ERROR = "TemperatureUpdaterService.Actions.UpdateError";
    // update error extra
    public static final String EXTRA_UPDATE_ERROR = "TemperatureUpdaterService.Extra.UpdateError";

    private LocationManager mLocationManager;
    private Looper mServiceLooper;
    private Handler mServiceHandler;
    private Boolean mIsUpdatingTemperature;


    public static void startForUpdate(Context context) {
        final Intent intent = new Intent(context, TemperatureUpdaterService.class);
        intent.setAction(ACTION_UPDATE);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        HandlerThread thread = new HandlerThread("TemperatureUpdaterHandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);
        mIsUpdatingTemperature = false;
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_UPDATE.equals(intent.getAction()) && !mIsUpdatingTemperature) {
            if (ConnectivityUtils.isNetworkConnected(getApplicationContext())) {
                mIsUpdatingTemperature = true;
                // First get a new location
                getNewLocation();
            } else {
                broadcastErrorAndStop(R.string.error_message_network_not_connected);
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void getNewLocation() {
        final String provider = LocationUtils.getBestCoarseProvider(this);
        if (provider == null) {
            broadcastErrorAndStop(R.string.error_message_location_provider_not_found);
        } else {
            mLocationManager.requestSingleUpdate(provider, this, null);
        }
    }

    private void updateTemperature(final Location location) {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    final URLConnection connection = getTemperatureURLConnection(location);
                    final OpenWeatherMapParserResult parserResult = parseURLConnection(connection);
                    PreferenceUtils.storeTemperatureInCelsius(TemperatureUpdaterService.this, parserResult.getTemperatureValue());

                    // Broadcast change to the widget provider
                    Intent intent = new Intent(TemperatureUpdaterService.this, STWidgetProvider.class);
                    intent.setAction(STWidgetProvider.APPWIDGET_DATA_CHANGED);
                    sendBroadcast(intent);
                    mIsUpdatingTemperature = false;
                    TemperatureUpdaterService.this.stopSelf();
                } catch (SocketTimeoutException e) {
                    broadcastErrorAndStop(R.string.error_message_server_not_available);
                } catch (MalformedURLException e) {
                    broadcastErrorAndStop(R.string.error_message_malformed_url);
                } catch (IOException e) {
                    broadcastErrorAndStop(R.string.error_message_io_exception);
                } catch (XmlPullParserException e) {
                    broadcastErrorAndStop(R.string.error_message_xml_pull_parser_exception);
                }
            }
        });
    }

    private URLConnection getTemperatureURLConnection(Location location) throws IOException {
        final URL url = new URL(getResources().getString(R.string.url_open_weather_api, location.getLatitude(), location.getLongitude()));
        final URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(10000);
        urlConnection.setUseCaches(true);
        return urlConnection;
    }

    private OpenWeatherMapParserResult parseURLConnection(URLConnection connection) throws IOException, XmlPullParserException {
        final InputStream inputStream = new BufferedInputStream(connection.getInputStream());
        final OpenWeatherMapParser parser = new OpenWeatherMapParser();
        return parser.parse(inputStream);
    }

    private void broadcastErrorAndStop(int resourceId) {
        final String message = getString(resourceId);
        final Intent intent = new Intent(ACTION_UPDATE_ERROR);
        intent.putExtra(EXTRA_UPDATE_ERROR, message);
        sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {
        updateTemperature(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
