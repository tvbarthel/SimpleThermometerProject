package fr.tvbarthel.apps.simplethermometer.utils;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityUtils {

	/**
	 * Check if a network connection is available
	 *
	 * @return true if a network connection is available, false otherwise.
	 */
	public static boolean isNetworkConnected(Context context) {
		//Retrieve the instance of the connectivity manager
		final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
		//Retrieve info about the currently active default network
		final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
}
