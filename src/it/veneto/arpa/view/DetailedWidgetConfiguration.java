package it.veneto.arpa.view;

import it.veneto.arpa.R;
import it.veneto.arpa.controller.Controller;
import it.veneto.arpa.controller.GeolocationService;
import android.preference.PreferenceActivity;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.appwidget.AppWidgetManager;
import android.widget.Button;
import android.view.View;
import android.preference.ListPreference;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Looper;

/**
 * Class used to show the detailed widgets configuration
 * @author Luca
 *
 */
public class DetailedWidgetConfiguration extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Looper myLooper;

    /**
     * @see Android PreferenceActivity.OnCreate(Bundle savedInstanceState)
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        // Control the widget configuration
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                               AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave an intent without the widget id, just close
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        Thread thread = new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                Controller.getInstance().plist(getApplicationContext());
                myLooper = Looper.myLooper();
                Looper.loop();
                myLooper.quit();
            }
        });
        thread.start();
        //Add interface
        addPreferencesFromResource(R.xml.configuration);
        setContentView(it.veneto.arpa.R.layout.detailed_widget_conf);
        //Create adapter for list_view
        final ListPreference listPreference = (ListPreference) findPreference("city_zone");
        setListPreferenceData(listPreference);
        //Add listener on cities change
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);
        //Add on_click action for save button
        Button button = (Button) findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultValue = new Intent();
                resultValue.putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);

                if (prefs.getString("city_zone", "").equals("geo")) {
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if(!locationManager.isProviderEnabled (LocationManager.NETWORK_PROVIDER)) {
                        Intent dialogIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(dialogIntent);
                    }
                    else if(netInfo != null && netInfo.isConnectedOrConnecting()) {
                        Controller.getInstance().plist(getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(), GeolocationService.class );
                        intent.putExtra("widgetType", "d");
                        intent.putExtra("widgetId", mAppWidgetId);
                        startService(intent);
                        //GeoLocation.getInstance().startLocation(getApplicationContext(), mAppWidgetId, "d");
                        Controller.getInstance().saveWidgetGeoLocal(getApplicationContext(), mAppWidgetId);
                        Controller.getInstance().updateBulletinAlarm(getApplicationContext(), "d");
                        Controller.getInstance().updateWidgetAlarm(getApplicationContext(), "d");
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Internet connection not detected", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (prefs.getString("city_zone", null) != null) {
                    Controller.getInstance().updateWidgetFromConfiguration(getApplicationContext(), mAppWidgetId, listPreference.getEntry().toString());
                    Controller.getInstance().updateBulletinAlarm(getApplicationContext(), "d");
                    Controller.getInstance().updateWidgetAlarm(getApplicationContext(), "d");
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Select city", Toast.LENGTH_SHORT).show();
                    ListPreference listPreference = (ListPreference) findPreference("city_zone");
                    setListPreferenceData(listPreference);
                    listPreference.setValueIndex(0);
                }
            }
        });
    }

    /**
     *
     * @param lp Android listpreferences to update
     */
    //Retrieve information about cities
    protected void setListPreferenceData(ListPreference lp) {
        CharSequence[] entries = Controller.getInstance().getLastCities(getApplicationContext());
        CharSequence[] entryValues = Controller.getInstance().getLastCitiesId(getApplicationContext());
        lp.setEntries(entries);
        lp.setEntryValues(entryValues);
        lp.setDefaultValue(entryValues[0]);
    }

    /**
     * On shared preference change
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        try {
            if (sharedPreferences.getString(key, "0").equals("new")) {
                sharedPreferences.edit().remove(key).commit();
                Intent myIntent = new Intent(DetailedWidgetConfiguration.this, Provinces.class);
                DetailedWidgetConfiguration.this.startActivityForResult(myIntent, 0);
            }
        }
        catch(ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the result of the activities launched
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 0) {
            ListPreference listPreference = (ListPreference) findPreference("city_zone");
            setListPreferenceData(listPreference);
            listPreference.setValueIndex(0);
        }
        else if (resultCode == 1) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}