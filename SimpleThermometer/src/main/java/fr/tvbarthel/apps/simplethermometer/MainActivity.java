package fr.tvbarthel.apps.simplethermometer;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import fr.tvbarthel.apps.simplethermometer.dialogfragments.AboutDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.ChangeColorDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.SharedPreferenceColorPickerDialogFragment;
import fr.tvbarthel.apps.simplethermometer.dialogfragments.TemperatureUnitPickerDialogFragment;
import fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserAsyncTask;
import fr.tvbarthel.apps.simplethermometer.openweathermap.OpenWeatherMapParserResult;

public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
		OpenWeatherMapParserAsyncTask.Listener, ChangeColorDialogFragment.Listener {

	/*
		Shared Preference Keys
	 */

	//Used to store the color of the background
	public static final String PREF_KEY_BACKGROUND_COLOR = "PrefKeyBackgroundColor";
	//Used to store the color of the text
	public static final String PREF_KEY_TEXT_COLOR = "PrefKeyTextColor";
	//Used to store the color of the icons
	public static final String PREF_KEY_ICON_COLOR = "PrefKeyIconColor";
	//Used to store the last retrieved temperature (in Celsius)
	public static final String PREF_KEY_LAST_TEMPERATURE_IN_CELSIUS = "PrefKeylastTemperatureInCelsius";
	//Used to store the time of the last update (in Millis)
	public static final String PREF_KEY_LAST_UPDATE_TIME = "PrefKeyLastUpdateTime";
	//Used to store the temperature unit
	public static final String PREF_KEY_TEMPERATURE_UNIT_STRING = "PrefKeyTemperatureUnitString";

	/*
		Update Interval
	 */

	//automatic update interval (in Millis)
	public static final long UPDATE_INTERVAL_IN_MILLIS = 30 * 60 * 1000;
	//manual update interval (in Millis)
	public static final long UPDATE_INTERVAL_IN_MILLIS_MANUAL = 10 * 60 * 1000;

	/*
		UI Elements
	 */

	//Display the temperature with the unit symbol
	private TextView mTextViewTemperature;
	//Root View
	private RelativeLayout mRelativeLayoutBackground;
	//ImageView of the fair weather icon
	private ImageView mImageViewFair;
	//ImageView of the change weather icon
	private ImageView mImageViewChange;
	//ImageView of the rain weather icon
	private ImageView mImageViewRain;
	//ImageView of the storm weather icon
	private ImageView mImageViewStorm;

	/*
		Other
	 */

	//Default Shared Preferences used in the app
	private SharedPreferences mDefaultSharedPreferences;
	//An AsyncTask used to load the temperature
	private OpenWeatherMapParserAsyncTask mOpenWeatherMapResultLoader;
	//A single Toast used to display textToast
	private Toast mTextToast;

	/*
		Activity Overrides
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Retrieve the UI elements references
		mTextViewTemperature = (TextView) findViewById(R.id.textViewTemperature);
		mRelativeLayoutBackground = (RelativeLayout) findViewById(R.id.relativeLayout);
		mImageViewFair = (ImageView) findViewById(R.id.imageViewFair);
		mImageViewChange = (ImageView) findViewById(R.id.imageViewChange);
		mImageViewRain = (ImageView) findViewById(R.id.imageViewRain);
		mImageViewStorm = (ImageView) findViewById(R.id.imageViewStorm);

		//Retrieve the default shared preferences instance
		mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Listen to the shared preference changes
		mDefaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
		//Set the background color
		setBackgroundColor();
		//Set the text color
		setTextColor();
		//Set the icon color
		setIconColor();
		//Display the temperature
		displayTemperature();
		//refresh the temperature if it's outdated
		refreshTemperatureIfOutdated();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Stop listening to shared preference changes
		mDefaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		//hide Toast if displayed
		hideToastIfDisplayed();
		//Cancel and clear the AsyncTask used to load the temperature
		if (mOpenWeatherMapResultLoader != null) {
			mOpenWeatherMapResultLoader.cancel(true);
			mOpenWeatherMapResultLoader = null;
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
				ChangeColorDialogFragment.newInstance(getResources().getStringArray(R.array.change_color_options)
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/*
		SharedPreferences.OnSharedPreferenceChangeListener Override
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String sharedPreferenceKey) {
		//the shared preference with the key "sharedPreferenceKey" has changed
		if (sharedPreferenceKey.equals(PREF_KEY_BACKGROUND_COLOR)) {
			//Set the new background color stored in the SharedPreferences "sharedPreferences"
			setBackgroundColor(sharedPreferences);
		} else if (sharedPreferenceKey.equals(PREF_KEY_TEXT_COLOR)) {
			//Set the new text color stored in the SharedPreferences "sharedPreferences"
			setTextColor(sharedPreferences);
		} else if (sharedPreferenceKey.equals(PREF_KEY_ICON_COLOR)) {
			//Set the new icon color stored in the SharedPreferences "sharedPreferences"
			setIconColor(sharedPreferences);
		} else if (sharedPreferenceKey.equals(PREF_KEY_TEMPERATURE_UNIT_STRING)) {
			//Display the temperature with the new unit stored in the SharedPreferences "sharedPreferences"
			displayTemperature();
		}
	}

	/*
		OpenWeatherMapParserAsyncTask.Listener Override
	 */
	@Override
	public void onWeatherLoadingSuccess(OpenWeatherMapParserResult result) {
		if (result.getTemperatureValue() != null) {
			//Store the temperature and the time of the update
			//in mDefaultSharedPreferences
			final SharedPreferences.Editor editor = mDefaultSharedPreferences.edit();
			editor.putFloat(PREF_KEY_LAST_TEMPERATURE_IN_CELSIUS, result.getTemperatureValue());
			editor.putLong(PREF_KEY_LAST_UPDATE_TIME, System.currentTimeMillis());
			editor.commit();
		}
		//Update the displayed temperature
		displayTemperature();
		//reset the weather loader
		resetOpenWeatherMapLoader();
	}

	@Override
	public void onWeatherLoadingFail(int stringResourceId) {
		//Show the reason of the failure
		makeTextToast(stringResourceId);
		//Display the last known temperature
		displayTemperature();
		//reset the weather loader
		resetOpenWeatherMapLoader();
	}

	@Override
	public void onWeatherLoadingProgress(int progress) {
		//Display the weather loader progress
		mTextViewTemperature.setText(String.format(getString(R.string.message_loading_progress), progress));
	}

	@Override
	public void onWeatherLoadingCancelled() {
		//reset the weather loader
		resetOpenWeatherMapLoader();
		//Display the last known temperature
		displayTemperature();
	}


	/*
		ChangeColorDialogFragment.Listener Override
	 */
	@Override
	public void onChangeColorRequested(int which) {
		String sharedPrefColor = PREF_KEY_BACKGROUND_COLOR;
		if (which == 1) {
			sharedPrefColor = PREF_KEY_TEXT_COLOR;
		} else if (which == 2) {
			sharedPrefColor = PREF_KEY_ICON_COLOR;
		}
		pickSharedPreferenceColor(sharedPrefColor);
	}

	/**
	 * Display the temperature with a unit symbol.
	 * The temperature and the unit are retrieved from {@code mDefaultSharedPreferences}
	 */
	private void displayTemperature() {
		//Retrieve the temperature (in Celsius)
		String temperatureUnit = mDefaultSharedPreferences.getString(PREF_KEY_TEMPERATURE_UNIT_STRING,
				getString(R.string.temperature_unit_celsius_symbol));
		//Retrieve the unit symbol
		Float temperatureFlt = mDefaultSharedPreferences.getFloat(PREF_KEY_LAST_TEMPERATURE_IN_CELSIUS, 20);

		if (temperatureUnit.equals(getString(R.string.temperature_unit_fahrenheit_symbol))) {
			//Convert from Celsius to Fahrenheit
			temperatureFlt += 32f;
		} else if (temperatureUnit.equals(getString(R.string.temperature_unit_kelvin_symbol))) {
			//Convert from Celsius to Kelvin
			temperatureFlt += 273.15f;
		}
		//Format the temperature with only one decimal
		final String temperatureStr = new DecimalFormat("#.#").format(temperatureFlt);
		mTextViewTemperature.setText(temperatureStr + temperatureUnit);
	}

	/**
	 * Retrieve the icon color stored in a {@link android.content.SharedPreferences},
	 * and apply a color filter to the icon ImageViews.
	 *
	 * @param sharedPreferences the {@link android.content.SharedPreferences} used to retrieve the icon color
	 */
	private void setIconColor(SharedPreferences sharedPreferences) {
		//Retrieve the icon color
		final int iconColor = sharedPreferences.getInt(PREF_KEY_ICON_COLOR,
				getResources().getColor(R.color.white));
		//Apply a color Filter to the four ImageViews
		mImageViewFair.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
		mImageViewChange.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
		mImageViewRain.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
		mImageViewStorm.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
	}

	private void setIconColor() {
		setIconColor(mDefaultSharedPreferences);
	}

	/**
	 * Retrieve the text color stored in a {@link android.content.SharedPreferences},
	 * and set it to the textViews.
	 *
	 * @param sharedPreferences the {@link android.content.SharedPreferences} used to retrieve the text color
	 */
	private void setTextColor(SharedPreferences sharedPreferences) {
		//Retrieve the text color
		final int textColor = sharedPreferences.getInt(PREF_KEY_TEXT_COLOR,
				getResources().getColor(R.color.black));
		//Set the text color to the temperature textView
		mTextViewTemperature.setTextColor(textColor);
	}

	private void setTextColor() {
		setTextColor(mDefaultSharedPreferences);
	}


	/**
	 * Retrieve the background color stored in a {@link android.content.SharedPreferences},
	 * and use it to set the background color of {@code mRelativeLayoutBackground}.
	 *
	 * @param sharedPreferences the {@link android.content.SharedPreferences} used to retrieve the background color
	 */
	private void setBackgroundColor(SharedPreferences sharedPreferences) {
		mRelativeLayoutBackground.setBackgroundColor(sharedPreferences.getInt(PREF_KEY_BACKGROUND_COLOR,
				getResources().getColor(R.color.holo_blue)));
	}

	private void setBackgroundColor() {
		setBackgroundColor(mDefaultSharedPreferences);
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
		SharedPreferenceColorPickerDialogFragment.newInstance(preferenceKey,
				getResources().getStringArray(R.array.pref_color_list_names),
				getResources().getIntArray(R.array.pref_color_list_colors)).show(getSupportFragmentManager(), null);
	}

	/**
	 * Check if a network connection is available
	 *
	 * @return true if a network connection is available, false otherwise.
	 */
	private boolean isNetworkConnected() {
		//Retrieve the instance of the connectivity manager
		final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		//Retrieve info about the currently active default network
		final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
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
		//Retrieve the current time and the time of the last update (in Millis)
		final long now = System.currentTimeMillis();
		final long lastUpdate = mDefaultSharedPreferences.getLong(PREF_KEY_LAST_UPDATE_TIME, 0);

		//Get the update Interval
		long updateInterval = UPDATE_INTERVAL_IN_MILLIS;
		if (manualRefresh) updateInterval = UPDATE_INTERVAL_IN_MILLIS_MANUAL;

		//if the temperature is outdated, try an update.
		if (now - lastUpdate > updateInterval) {
			if (!isNetworkConnected()) {
				//there is no connection available
				makeTextToast(R.string.error_message_network_not_connected);
			} else if (mOpenWeatherMapResultLoader == null) {
				//there is no running weather loader

				//retrieve an instance of the LocationManager
				final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

				//Get a location with a coarse accuracy
				final Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
				final String provider = locationManager.getBestProvider(criteria, true);
				if (provider == null) return;
				final Location location = locationManager.getLastKnownLocation(provider);

				if (location == null) {
					//no location is available
					makeTextToast(R.string.error_message_location_not_found);
				} else {
					//Retrieve the latitude and the longitude and execute a weather loader
					final double latitude = location.getLatitude();
					final double longitude = location.getLongitude();
					mOpenWeatherMapResultLoader = new OpenWeatherMapParserAsyncTask(this);
					mOpenWeatherMapResultLoader.execute(String.format(getResources().getString(R.string.url_open_weather_api), latitude, longitude));
				}
			}
		}
	}

	private void refreshTemperatureIfOutdated() {
		refreshTemperatureIfOutdated(false);
	}

	/**
	 * Reset the weather loader
	 */
	public void resetOpenWeatherMapLoader() {
		if (mOpenWeatherMapResultLoader != null) {
			mOpenWeatherMapResultLoader.cancel(true);
			mOpenWeatherMapResultLoader.setListener(null);
			mOpenWeatherMapResultLoader = null;
		}
	}

	/**
	 * Show the about information in a {@link fr.tvbarthel.apps.simplethermometer.dialogfragments.AboutDialogFragment}
	 */
	private void displayAbout() {
		new AboutDialogFragment().show(getSupportFragmentManager(), null);
	}

}
