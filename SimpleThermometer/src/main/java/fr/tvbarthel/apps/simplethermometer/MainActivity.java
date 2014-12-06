package fr.tvbarthel.apps.simplethermometer;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

import fr.tvbarthel.apps.simplethermometer.dialogfragments.AboutDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.ListPickerDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.MoreAppsDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.OpacityDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.SharedPreferenceColorPickerDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.TemperatureUnitPickerDialogFragment;
import fr.tvbarthel.apps.simplethermometer.services.TemperatureUpdaterService;
import fr.tvbarthel.apps.simplethermometer.utils.ColorUtils;
import fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils;
import fr.tvbarthel.apps.simplethermometer.widget.STWidgetProvider;

public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        ListPickerDialogFragment.Listener {


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
    //The root to use as background
    private View mRootView;


    // A single Toast used to display textToast
    private Toast mTextToast;
    // A simple broadcast receiver to catch error from the updater service
    private BroadcastReceiver mBroadcastReceiver;

	/*
        Activity Overrides
	 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        //Retrieve the UI elements references
        mTextViewTemperature = (TextView) findViewById(R.id.activity_main_temperature);
        mLeftLine = findViewById(R.id.activity_main_horizontal_line_left);
        mRightLine = findViewById(R.id.activity_main_horizontal_line_right);
        mEllipseBackground = (GradientDrawable) mTextViewTemperature.getBackground();
        mProgressBar = (ProgressBar) findViewById(R.id.activity_main_progress_bar);
        mRootView = findViewById(R.id.activity_main_root);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && TemperatureUpdaterService.ACTION_UPDATE_ERROR.equals(intent.getAction())) {
                    makeTextToast(intent.getStringExtra(TemperatureUpdaterService.EXTRA_UPDATE_ERROR));
                    displayLastKnownTemperature();
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            initRebound();
        }

        initRootPadding();
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
        // Register a broadcast receiver for the error from the TemperatureUpdaterService
        final IntentFilter filter = new IntentFilter(TemperatureUpdaterService.ACTION_UPDATE_ERROR);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Stop listening to shared preference changes
        PreferenceUtils.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        //hide Toast if displayed
        hideToastIfDisplayed();
        // Unregister the broadcast receiver
        unregisterReceiver(mBroadcastReceiver);
    }

    private void initRootPadding() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Resources resources = getResources();
            final boolean isPortrait = resources.getBoolean(R.bool.is_portrait);
            final ActionBar actionBar = getSupportActionBar();

            final ViewTreeObserver vto = mRootView.getViewTreeObserver();
            if (vto.isAlive()) {
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mRootView.getViewTreeObserver().removeOnPreDrawListener(this);

                        int paddingBottom = mRootView.getPaddingBottom();
                        int paddingTop = mRootView.getPaddingTop();
                        int paddingRight = mRootView.getPaddingRight();
                        int paddingLeft = mRootView.getPaddingLeft();

                        // Add the status bar height to the top padding.
                        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
                        if (resourceId > 0) {
                            paddingTop += resources.getDimensionPixelSize(resourceId);
                        }

                        if (isPortrait) {
                            // Add the navigation bar height to the bottom padding.
                            resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                            if (resourceId > 0) {
                                paddingBottom += resources.getDimensionPixelSize(resourceId);
                            }
                        } else {
                            // Add the navigation bar width to the right padding.
                            resourceId = resources.getIdentifier("navigation_bar_width", "dimen", "android");
                            if (resourceId > 0) {
                                paddingRight += resources.getDimensionPixelSize(resourceId);
                            }
                        }

                        mRootView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
                        return true;
                    }
                });
            }
        }
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
        if (sharedPreferenceKey.equals(PreferenceUtils.PREF_KEY_BACKGROUND_COLOR) ||
                PreferenceUtils.PREF_KEY_BACKGROUND_OPACITY.equals(sharedPreferenceKey)) {
            //Set the new background color stored in the SharedPreferences "sharedPreferences"
            setBackgroundColor();
            broadcastChangeToWidgets = true;
        } else if (sharedPreferenceKey.equals(PreferenceUtils.PREF_KEY_TEXT_COLOR) ||
                PreferenceUtils.PREF_KEY_TEXT_OPACITY.equals(sharedPreferenceKey)) {
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
        } else if (sharedPreferenceKey.equals(PreferenceUtils.PREF_KEY_LAST_UPDATE_TIME)) {
            // A new update have been completed
            // Display the temperature with the new value stored in the SharedPreferences "sharedPreferences"
            displayLastKnownTemperature();
        }

        if (broadcastChangeToWidgets) {
            //A change has to be propagate to the app widgets
            Intent intent = new Intent(this, STWidgetProvider.class);
            intent.setAction(STWidgetProvider.APPWIDGET_DATA_CHANGED);
            sendBroadcast(intent);
        }

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
        PreferenceUtils.PreferenceId preferenceId = PreferenceUtils.PreferenceId.BACKGROUND;
        if (which == 1) {
            preferenceId = PreferenceUtils.PreferenceId.TEXT;
        } else if (which == 2) {
            preferenceId = PreferenceUtils.PreferenceId.FOREGROUND;
        }
        OpacityDialogFragment.newInstance(preferenceId).show(getSupportFragmentManager(), null);
    }

    private void onColorChangeRequested(int which) {
        PreferenceUtils.PreferenceId preferenceId = PreferenceUtils.PreferenceId.BACKGROUND;
        if (which == 1) {
            preferenceId = PreferenceUtils.PreferenceId.TEXT;
        } else if (which == 2) {
            preferenceId = PreferenceUtils.PreferenceId.FOREGROUND;
        }
        pickSharedPreferenceColor(preferenceId);
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
        final int textAlpha = PreferenceUtils.getPreferedAlpha(this, PreferenceUtils.PreferenceId.TEXT);
        //Set the text color to the temperature textView
        mTextViewTemperature.setTextColor(ColorUtils.addAlphaToColor(textColor, textAlpha));
    }


    /**
     * Retrieve the background color stored in a {@link android.content.SharedPreferences},
     * and use it to set the background color of {@code mRelativeLayoutBackground}.
     */
    private void setBackgroundColor() {
        final int backgroundColor = PreferenceUtils.getPreferedColor(this,
                PreferenceUtils.PreferenceId.BACKGROUND);
        final int backgroundAlpha = PreferenceUtils.getPreferedAlpha(this,
                PreferenceUtils.PreferenceId.BACKGROUND);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mRootView.setBackgroundDrawable(new ColorDrawable(
                    ColorUtils.addAlphaToColor(backgroundColor, backgroundAlpha)));
        } else {
            mRootView.setBackground(new ColorDrawable(
                    ColorUtils.addAlphaToColor(backgroundColor, backgroundAlpha)));
        }

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
     * @param preferenceId the {@link fr.tvbarthel.apps.simplethermometer.utils.PreferenceUtils.PreferenceId} for which the color will be picked.
     */
    private void pickSharedPreferenceColor(PreferenceUtils.PreferenceId preferenceId) {
        SharedPreferenceColorPickerDialogFragment.newInstance(preferenceId, ColorUtils.getColorPicks(this))
                .show(getSupportFragmentManager(), null);
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
        if (PreferenceUtils.isTemperatureOutdated(this, manualRefresh)) {
            if (manualRefresh) {
                mTextViewTemperature.setText("");
                mProgressBar.setVisibility(View.VISIBLE);
            }
            TemperatureUpdaterService.startForUpdate(this);
        } else if (manualRefresh) {
            makeTextToast(R.string.error_message_temperature_up_to_date);
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initRebound() {
        final SpringSystem springSystem = SpringSystem.create();
        final Spring spring = springSystem.createSpring();
        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 3));
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                mTextViewTemperature.setScaleX(scale);
                mTextViewTemperature.setScaleY(scale);
            }
        });
        mTextViewTemperature.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        spring.setEndValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        spring.setEndValue(0);
                        break;
                }
                return true;
            }
        });
    }

}
