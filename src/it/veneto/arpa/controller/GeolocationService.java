package it.veneto.arpa.controller;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.Service;

/**
 * Service used to update widgtes that use geolocation
 * @author Luca
 *
 */
public class GeolocationService extends Service implements LocationListener {
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    String lat;
    String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    //int widgetIdD = 0;
    //int widgetIdS = 0;
    //private static final String PREFERENCES = "service.geo.pref";
    private static final long updateTime = 60 * 60 * 1000;
    private static final float updateMt = 500;

    /**
     * Update widget when locationManager find new Location
     * @param location is the location find by LocationManager
     * @see Android LocationListner.onLocationChanged(Location location)
     */
    @Override
    public void onLocationChanged(Location location) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (Controller.getInstance().stopService() || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.removeUpdates(this);
            locationManager = null;
            stopSelf();
        }
        else if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Intent intentDetailed = new Intent(getApplicationContext(), it.veneto.arpa.view.DetailedWidgetProvider.class);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                intentDetailed.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            }

            intentDetailed.setAction("it.veneto.arpa.geo");
            intentDetailed.putExtra("LAT", location.getLatitude() + "");
            intentDetailed.putExtra("LON", location.getLongitude() + "");
            sendBroadcast(intentDetailed);
            Intent intentSimple = new Intent(getApplicationContext(), it.veneto.arpa.view.SimpleWidgetProvider.class);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                intentSimple.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            }

            intentSimple.setAction("it.veneto.arpa.geo");
            intentSimple.putExtra("LAT", location.getLatitude() + "");
            intentSimple.putExtra("LON", location.getLongitude() + "");
            sendBroadcast(intentSimple);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * @see Android Service.onStartCommand(Intent intent, int flags, int startId)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        locationManager. removeUpdates(this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updateTime, updateMt, this);
        Controller.getInstance().stopService(false);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        locationManager. removeUpdates(this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updateTime, updateMt, this);
        Controller.getInstance().stopService(false);
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
        locationManager = null;
    }
}
