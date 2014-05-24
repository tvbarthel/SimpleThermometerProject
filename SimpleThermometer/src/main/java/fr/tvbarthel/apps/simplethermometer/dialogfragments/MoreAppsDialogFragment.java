package fr.tvbarthel.apps.simplethermometer.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.tvbarthel.apps.simplethermometer.R;
import fr.tvbarthel.apps.simplethermometer.models.App;

/**
 * A simple {@link android.support.v4.app.DialogFragment} used to promote our other applications.
 */
public class MoreAppsDialogFragment extends DialogFragment {

    private static final String URI_ROOT_MARKET = "market://details?id=";
    private static final String URI_ROOT_PLAY_STORE = "http://play.google.com/store/apps/details?id=";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final ListView listView = (ListView) inflater.inflate(R.layout.dialog_more_apps, null);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setPositiveButton(android.R.string.ok, null);
        dialogBuilder.setTitle(R.string.dialog_more_apps_title);
        dialogBuilder.setView(listView);
        dialogBuilder.setInverseBackgroundForced(true);

        App chaseWhisply = new App();
        chaseWhisply.setLogoResourceId(R.drawable.ic_chase_whisply);
        chaseWhisply.setNameResourceId(R.string.dialog_more_apps_chase_whisply_app_name);
        chaseWhisply.setPackageNameResourceId(R.string.dialog_more_apps_chase_whisply_package_name);

        App googlyZoo = new App();
        googlyZoo.setLogoResourceId(R.drawable.ic_googly_zoo);
        googlyZoo.setNameResourceId(R.string.dialog_more_apps_googly_zoo_app_name);
        googlyZoo.setPackageNameResourceId(R.string.dialog_more_apps_googly_zoo_package_name);

        App simpleWeatherForecast = new App();
        simpleWeatherForecast.setLogoResourceId(R.drawable.ic_simple_weather_forecast);
        simpleWeatherForecast.setNameResourceId(R.string.dialog_more_apps_simple_weather_forecast_app_name);
        simpleWeatherForecast.setPackageNameResourceId(R.string.dialog_more_apps_simple_weather_forecast_package_name);

        final ArrayList<App> apps = new ArrayList<App>();
        apps.add(chaseWhisply);
        apps.add(googlyZoo);
        apps.add(simpleWeatherForecast);

        listView.setAdapter(new MoreAppsAdapter(getActivity(), apps));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                App appClicked = apps.get(position);
                launchPlayStoreDetails(getResources().getString(appClicked.getPackageNameResourceId()));
            }
        });

        return dialogBuilder.create();
    }

    private void launchPlayStoreDetails(String appPackageName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_ROOT_MARKET + appPackageName)));
        } catch (android.content.ActivityNotFoundException activityNotFoundException) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URI_ROOT_PLAY_STORE + appPackageName)));
        }
    }

    /**
     * A simple {@link android.widget.ArrayAdapter} used to adapt {@link fr.tvbarthel.apps.simplethermometer.models.App}.
     */
    private static class MoreAppsAdapter extends ArrayAdapter<App> {

        public MoreAppsAdapter(Context context, List<App> apps) {
            super(context, R.layout.row_more_apps, apps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RelativeLayout appView = (RelativeLayout) convertView;
            App app = getItem(position);

            if (appView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                appView = (RelativeLayout) inflater.inflate(R.layout.row_more_apps, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.appName = (TextView) appView.findViewById(R.id.row_more_apps_name);
                holder.appLogo = (ImageView) appView.findViewById(R.id.row_more_apps_logo);
                appView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) appView.getTag();
            holder.appName.setText(app.getNameResourceId());
            holder.appLogo.setImageResource(app.getLogoResourceId());

            return appView;
        }

        private static class ViewHolder {
            TextView appName;
            ImageView appLogo;
        }
    }
}
