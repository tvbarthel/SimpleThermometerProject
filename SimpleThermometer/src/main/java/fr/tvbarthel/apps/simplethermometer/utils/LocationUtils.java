package fr.tvbarthel.apps.simplethermometer.utils;

import android.app.Service;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

/**
 * A simple utility class used for location.
 */
public final class LocationUtils {

    public static String getBestCoarseProvider(Context context) {
        //retrieve an instance of the LocationManager
        final LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
        //Get a location with a coarse accuracy
        final Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        return locationManager.getBestProvider(criteria, true);
    }

    public static Location getLastKnownLocation(Context context, String provider) {
        Location result = null;
        //retrieve an instance of the LocationManager
        final LocationManager locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
        // Retrieve the location from the provider
        result = locationManager.getLastKnownLocation(provider);
        return result;
    }

    // Non-instantiable class
    private LocationUtils() {
    }
}
