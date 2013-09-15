package fr.tvbarthel.apps.simplethermometer.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class STWidgetProvider extends AppWidgetProvider {

	public static final String APPWIDGET_DATA_CHANGED =
			"fr.vbarthel.apps.simplethermometer.widget.STWidgetProvider.DataChanged";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		updateAppWidgets(context, appWidgetIds, true);
	}

	private void updateAppWidgets(Context context, int[] appWidgetIds, boolean reloadTemperature) {
		final Intent intent = new Intent(context.getApplicationContext(), STWidgetUpdateService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
		intent.putExtra(STWidgetUpdateService.EXTRA_RELOAD_TEMPERATURE, reloadTemperature);
		context.startService(intent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		final String action = intent.getAction();

		if (STWidgetProvider.APPWIDGET_DATA_CHANGED.equals(action)) {
			final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
			final ComponentName thisWidget = new ComponentName(context.getApplicationContext(), STWidgetProvider.class);
			final int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
			updateAppWidgets(context, allWidgetIds, false);
		}
	}
}
