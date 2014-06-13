package fr.tvbarthel.apps.simplethermometer.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import fr.tvbarthel.apps.simplethermometer.MainActivity;
import fr.tvbarthel.apps.simplethermometer.R;
import fr.tvbarthel.apps.simplethermometer.utils.ColorUtils;
import fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils;

/**
 * A {@link android.app.Service} used by the {@link fr.tvbarthel.apps.simplethermometer.widget.STWidgetProvider}
 * to update the Simple Thermometer Widgets.
 */
public class STWidgetUpdateService extends Service {

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
        mAppWidgetManager = AppWidgetManager.getInstance(this);

        if (intent != null) {
            mAllWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            updateAppWidgets();
        }

        stopSelf();

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
        final Context applicationContext = getApplicationContext();
        final String temperature = PreferenceUtils.getTemperatureAsString(applicationContext);
        final int textColor = PreferenceUtils.getPreferedColor(applicationContext, PreferenceUtils.PreferenceId.TEXT);
        final int textAlpha = PreferenceUtils.getPreferedAlpha(applicationContext, PreferenceUtils.PreferenceId.TEXT);
        final int foregroundColor = PreferenceUtils.getPreferedColor(applicationContext, PreferenceUtils.PreferenceId.FOREGROUND);
        final int foregroundAlpha = PreferenceUtils.getPreferedAlpha(applicationContext, PreferenceUtils.PreferenceId.FOREGROUND);

        //Update all the app widgets
        for (int widgetId : mAllWidgetIds) {
            final RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.widget);

            //Use the stored values to update the app widget
            remoteViews.setTextViewText(R.id.widget_temperature, temperature);
            remoteViews.setTextColor(R.id.widget_temperature, ColorUtils.addAlphaToColor(textColor, textAlpha));
            remoteViews.setInt(R.id.widget_foreground, "setColorFilter", foregroundColor);
            remoteViews.setInt(R.id.widget_foreground, "setAlpha", foregroundAlpha);

            //Add a clickIntent on the app widget
            //This Intent will launch the SimpleThermometer Application
            final Intent clickIntent = new Intent(getApplicationContext(), MainActivity.class);
            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent);

            //Update the app widget
            mAppWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
