package fr.tvbarthel.apps.simplethermometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.tvbarthel.apps.simplethermometer.dialogfragments.AboutDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.ListPickerDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.MoreAppsDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.OpacityDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.SharedPreferenceColorPickerDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.TemperatureUnitPickerDialogFragment;
import fr.tvbarthel.apps.simplethermometer.models.ColorPick;
import fr.tvbarthel.apps.simplethermometer.utils.ColorUtils;
import fr.tvbarthel.apps.simplethermometer.utils.ConnectivityUtils;
import fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils;
import fr.tvbarthel.apps.simplethermometer.widget.STWidgetProvider;

public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        ListPickerDialogFragment.Listener, TemperatureLoader.Listener {


    private static final int CHOICE_ID_COLORS = 100;
    private static final int CHOICE_ID_OPACITIES = 200;

	/*
        UI Elements
	 */

    //Display the temperature with the unit symbol
    private TextView mTextViewTemperature;
    //Text Background
    private GradientDrawable mEllipseBackground;
    //Left Line
    private View mLeftLine;
    //Right Line
    private View mRightLine;
    //Progress Bar
    private ProgressBar mProgressBar;

	/*
        Other
	 */
    //An AsyncTask used to start the temperature
    private TemperatureLoader mTemperatureLoader;
    //A single Toast used to display textToast
    private Toast mTextToast;

	/*
        Activity Overrides
	 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTemperatureLoader = new TemperatureLoader(this, getApplicationContext());

        //Retrieve the UI elements references
        mTextViewTemperature = (TextView) findViewById(R.id.activity_main_temperature);
        mLeftLine = findViewById(R.id.activity_main_horizontal_line_left);
        mRightLine = findViewById(R.id.activity_main_horizontal_line_right);
        mEllipseBackground = (GradientDrawable) mTextViewTemperature.getBackground();
        mProgressBar = (ProgressBar) findViewById(R.id.activity_main_progress_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Listen to the shared preference changes
        PreferenceUtils.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        //Set the background color
        setBackgroundColor();
        //Set the text color
        setTextColor();
        //Set the foreground color
        setForegroundColor();
        //Display the temperature
        displayLastKnownTemperature();
        //refresh the temperature if it's outdated
        refreshTemperatureIfOutdated();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop listening to shared preference changes
        PreferenceUtils.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        //hide Toast if displayed
        hideToastIfDisplayed();
        //Pause the temperature Loader
        mTemperatureLoader.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_action_set_color:
                //Ask for the color you want to change through an AlertDialogFragment
                ListPickerDialogFragment.newInstance(CHOICE_ID_COLORS, getResources().getStringArray(R.array.change_color_options)
                ).show(getSupportFragmentManager(), null);
                return true;
            case R.id.menu_item_action_temperature_unit:
                //Ask for the temperature unit you want to use
                pickTemperatureUnit();
                return true;
            case R.id.menu_item_action_manual_refresh:
                //Manually update the temperature if it's outdated
                refreshTemperatureIfOutdated(true);
                return true;
            case R.id.menu_item_action_about:
                //Show the about AlertDialogFragment
                displayAbout();
                return true;
            case R.id.menu_item_action_report_a_problem:
                return handleReportAProblem();
            case R.id.menu_item_action_more_apps:
                return handleMoreApps();
            case R.id.menu_item_action_set_opacity:
                return handleSetOpacity();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        SharedPreferences.OnSharedPreferenceChangeListener Override
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String sharedPreferenceKey) {
        boolean broadcastChangeToWidgets = false;
        //the shared preference with the key "sharedPreferenceKey" has changed
        if (sharedPreferenceKey.equals(PreferenceUtils.PREF_KEY_BACKGROUND_COLOR)) {
            //Set the new background color stored in the SharedPreferences "sharedPreferences"
            setBackgroundColor();
            broadcastChangeToWidgets = true;
        } else if (sharedPreferenceKey.equals(PreferenceUtils.PREF_KEY_TEXT_COLOR)) {
            //Set the new text color stored in the SharedPreferences "sharedPreferences"
            setTextColor();
            broadcastChangeToWidgets = true;
        } else if (sharedPreferenceKey.equals(PreferenceUtils.PREF_KEY_FOREGROUND_COLOR) ||
                PreferenceUtils.PREF_KEY_FOREGROUND_OPACITY.equals(sharedPreferenceKey)) {
            //Set the new foreground color stored in the SharedPreferences "sharedPreferences"
            setForegroundColor();
            broadcastChangeToWidgets = true;
        } else if (sharedPreferenceKey.equals(PreferenceUtils.PREF_KEY_TEMPERATURE_UNIT_STRING)) {
            //Display the temperature with the new unit stored in the SharedPreferences "sharedPreferences"
            displayLastKnownTemperature();
            broadcastChangeToWidgets = true;
        } else if (sharedPreferenceKey.equals(PreferenceUtils.PREF_KEY_LAST_TEMPERATURE_IN_CELSIUS)) {
            //Display the temperature with the new value stored in the SharedPreferences "sharedPreferences"
            //This mainly happens when the App is displayed and an app widget background service
            //update the temperature value.
            displayLastKnownTemperature();
            broadcastChangeToWidgets = true;
        }

        if (broadcastChangeToWidgets) {
            //A change has to be propagate to the app widgets
            Intent intent = new Intent(this, STWidgetProvider.class);
            intent.setAction(STWidgetProvider.APPWIDGET_DATA_CHANGED);
            sendBroadcast(intent);
        }

    }

	/*
        TemperatureLoader.Listener Override
	 */

    @Override
    public void onTemperatureLoadingSuccess() {
        //The temperature has been correctly loaded
        //and stored in the defaultSharedPreferences
        //so the last known temperature should be the
        //new temperature that has just been retrieved.
        displayLastKnownTemperature();
    }

    @Override
    public void onTemperatureLoadingProgress(int progress) {
        //Display the weather loader progress
        mTextViewTemperature.setText("");
        mProgressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onTemperatureLoadingFail(int stringResourceId) {
        //Show the reason of the failure
        makeTextToast(stringResourceId);
        //Display the last known temperature
        displayLastKnownTemperature();
    }

    @Override
    public void onTemperatureLoadingCancelled() {
        //Display the last known temperature
        displayLastKnownTemperature();
    }


    /*
        ChangeColorDialogFragment.Listener Override
     */
    @Override
    public void onChoiceSelected(int choiceId, int which) {
        if (choiceId == CHOICE_ID_COLORS) {
            onColorChangeRequested(which);
        } else if (choiceId == CHOICE_ID_OPACITIES) {
            onOpacityChangeRequested(which);
        }
    }

    private void onOpacityChangeRequested(int which) {
        String sharedPrefColor = PreferenceUtils.PREF_KEY_BACKGROUND_OPACITY;
        if (which == 1) {
            sharedPrefColor = PreferenceUtils.PREF_KEY_TEXT_OPACITY;
        } else if (which == 2) {
            sharedPrefColor = PreferenceUtils.PREF_KEY_FOREGROUND_OPACITY;
        }
        OpacityDialogFragment.newInstance(sharedPrefColor).show(getSupportFragmentManager(), null);
    }

    private void onColorChangeRequested(int which) {
        String sharedPrefColor = PreferenceUtils.PREF_KEY_BACKGROUND_COLOR;
        if (which == 1) {
            sharedPrefColor = PreferenceUtils.PREF_KEY_TEXT_COLOR;
        } else if (which == 2) {
            sharedPrefColor = PreferenceUtils.PREF_KEY_FOREGROUND_COLOR;
        }
        pickSharedPreferenceColor(sharedPrefColor);
    }

    private boolean handleSetOpacity() {
        // Ask which opacity to set
        ListPickerDialogFragment.newInstance(CHOICE_ID_OPACITIES, getResources().getStringArray(R.array.change_opacity_options)
        ).show(getSupportFragmentManager(), null);
        return true;
    }

    /**
     * Handle the "more apps" action.
     *
     * @return true is the action is handled, false otherwise.
     */
    private boolean handleMoreApps() {
        (new MoreAppsDialogFragment()).show(getSupportFragmentManager(), null);
        return true;
    }

    /**
     * Handle the "report a problem" action.
     * <p/>
     * Start a new activity to send a problem report.
     *
     * @return true if the action is handled, false otherwise.
     */
    private boolean handleReportAProblem() {
        final String email = getString(R.string.report_a_problem_email);
        final String subject = getString(R.string.report_a_problem_default_subject);
        final String uriString = getString(R.string.report_a_problem_uri,
                Uri.encode(email), Uri.encode(subject));
        final Uri mailToUri = Uri.parse(uriString);
        final Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(mailToUri);
        startActivity(intent);
        return true;
    }

    /**
     * Display the temperature with a unit symbol.
     * The temperature and the unit are retrieved from {@code mDefaultSharedPreferences}
     * so the temperature should be up to date.
     */
    private void displayLastKnownTemperature() {
        final String temperature = PreferenceUtils.getTemperatureAsString(this);
        mTextViewTemperature.setText(temperature);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Retrieve the foreground color stored in a {@link android.content.SharedPreferences},
     * and apply it to the foreground elements.
     */
    private void setForegroundColor() {
        //Retrieve the foreground color
        int foregroundColor = PreferenceUtils.getPreferedColor(this, PreferenceUtils.PreferenceId.FOREGROUND);
        // Retrieve the alpha
        int foregroundAlpha = PreferenceUtils.getPreferedAlpha(this, PreferenceUtils.PreferenceId.FOREGROUND);
        // Add the alpha to the color
        foregroundColor = ColorUtils.addAlphaToColor(foregroundColor, foregroundAlpha);

        //Apply color to the foreground elements
        mLeftLine.setBackgroundColor(foregroundColor);
        mRightLine.setBackgroundColor(foregroundColor);
        mEllipseBackground.setColor(foregroundColor);
    }

    /**
     * Retrieve the text color stored in a {@link android.content.SharedPreferences},
     * and set it to the textViews.
     */
    private void setTextColor() {
        //Retrieve the text color
        final int textColor = PreferenceUtils.getPreferedColor(this, PreferenceUtils.PreferenceId.TEXT);
        //Set the text color to the temperature textView
        mTextViewTemperature.setTextColor(textColor);
    }


    /**
     * Retrieve the background color stored in a {@link android.content.SharedPreferences},
     * and use it to set the background color of {@code mRelativeLayoutBackground}.
     */
    private void setBackgroundColor() {
        final int backgroundColor = PreferenceUtils.getPreferedColor(this, PreferenceUtils.PreferenceId.BACKGROUND);
        getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
    }

    /**
     * Show a {@link fr.tvbarthel.apps.simplethermometer.dialogfragments.TemperatureUnitPickerDialogFragment}
     * to ask the user to chose a temperature unit.
     */
    private void pickTemperatureUnit() {
        TemperatureUnitPickerDialogFragment.newInstance(getResources().getStringArray(R.array.pref_temperature_name),
                getResources().getStringArray(R.array.pref_temperature_unit_symbols)).show(getSupportFragmentManager(), null);
    }

    /**
     * Show a {@link fr.tvbarthel.apps.simplethermometer.dialogfragments.SharedPreferenceColorPickerDialogFragment}
     * to ask the user to chose a color to store for the sharedPreference with the key {@code preferenceKey}
     *
     * @param preferenceKey the {@link String} representing the sharedPreference key.
     */
    private void pickSharedPreferenceColor(String preferenceKey) {
        SharedPreferenceColorPickerDialogFragment.newInstance(preferenceKey, getColorPicks())
                .show(getSupportFragmentManager(), null);
    }

    /**
     * Get the colors that can be picked.
     *
     * @return an ArrayList of {@link fr.tvbarthel.apps.simplethermometer.models.ColorPick}
     */
    private ArrayList<ColorPick> getColorPicks() {
        final ArrayList<ColorPick> colorPicks = new ArrayList<ColorPick>();
        // Holo Blue
        colorPicks.add(new ColorPick(getString(R.string.color_name_holo_blue),
                getResources().getColor(R.color.holo_blue)));
        // Holo Blue Deep
        colorPicks.add(new ColorPick(getString(R.string.color_name_holo_blue_deep),
                getResources().getColor(R.color.holo_blue_deep)));
        // Holo Purple
        colorPicks.add(new ColorPick(getString(R.string.color_name_holo_purple),
                getResources().getColor(R.color.holo_purple)));
        // Holo Purple Deep
        colorPicks.add(new ColorPick(getString(R.string.color_name_holo_purple_deep),
                getResources().getColor(R.color.holo_purple_deep)));
        // Holo Green
        colorPicks.add(new ColorPick(getString(R.string.color_name_holo_green),
                getResources().getColor(R.color.holo_green)));
        // Holo Green Deep
        colorPicks.add(new ColorPick(getString(R.string.color_name_holo_green_deep),
                getResources().getColor(R.color.holo_green_deep)));
        // Holo Orange
        colorPicks.add(new ColorPick(getString(R.string.color_name_holo_orange),
                getResources().getColor(R.color.holo_orange)));
        // Holo Orange Deep
        colorPicks.add(new ColorPick(getString(R.string.color_name_holo_orange_deep),
                getResources().getColor(R.color.holo_orange_deep)));
        // Holo Red
        colorPicks.add(new ColorPick(getString(R.string.color_name_holo_red),
                getResources().getColor(R.color.holo_red)));
        // Holo Red Deep
        colorPicks.add(new ColorPick(getString(R.string.color_name_holo_red_deep),
                getResources().getColor(R.color.holo_red_deep)));
        // White
        colorPicks.add(new ColorPick(getString(R.string.color_name_white),
                getResources().getColor(R.color.white)));
        // Black
        colorPicks.add(new ColorPick(getString(R.string.color_name_black),
                getResources().getColor(R.color.black)));

        return colorPicks;
    }


    /**
     * Show a textToast.
     *
     * @param message the {@link String} to show.
     */
    private void makeTextToast(String message) {
        //hide mTextToast if showing
        hideToastIfDisplayed();
        //make a toast that just contains a text view
        mTextToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mTextToast.show();
    }

    private void makeTextToast(int stringId) {
        makeTextToast(getString(stringId));
    }

    /**
     * Hide {@code mTextToast} if displayed
     */
    private void hideToastIfDisplayed() {
        if (mTextToast != null) {
            mTextToast.cancel();
            mTextToast = null;
        }
    }


    /**
     * Refresh the temperature if it's outdated
     *
     * @param manualRefresh true if it's a manual refresh request
     */
    private void refreshTemperatureIfOutdated(boolean manualRefresh) {
        //Get the update Interval
        long updateInterval = TemperatureLoader.UPDATE_INTERVAL_IN_MILLIS;
        if (manualRefresh) updateInterval = TemperatureLoader.UPDATE_INTERVAL_IN_MILLIS_MANUAL;

        if (TemperatureLoader.isTemperatureOutdated(PreferenceUtils.getDefaultSharedPreferences(this), updateInterval)) {
            if (!ConnectivityUtils.isNetworkConnected(this)) {
                //there is no connection available
                makeTextToast(R.string.error_message_network_not_connected);
            } else {
                mTemperatureLoader.start();
            }
        }
    }

    private void refreshTemperatureIfOutdated() {
        refreshTemperatureIfOutdated(false);
    }


    /**
     * Show the about information in a {@link fr.tvbarthel.apps.simplethermometer.dialogfragments.AboutDialogFragment}
     */
    private void displayAbout() {
        new AboutDialogFragment().show(getSupportFragmentManager(), null);
    }

}
