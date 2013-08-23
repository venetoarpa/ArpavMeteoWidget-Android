package it.veneto.arpa.controller;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import it.veneto.arpa.util.parser.ParserBulletin;
import it.veneto.arpa.util.parser.ParserZone;
import it.veneto.arpa.util.parser.ParserGeoLocal;
import it.veneto.arpa.util.serialize.Serializer;
import it.veneto.arpa.model.Zone;
import it.veneto.arpa.model.Province;
import it.veneto.arpa.model.Bulletin;
import it.veneto.arpa.R;

import android.app.AlarmManager;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class controller of the MVC Pattern
 * @author Luca
 *
 */
public class Controller {

    protected Bulletin bulletinIT = null; //Bulletin in Italian
    protected Bulletin bulletinEN = null; //Bulletin in English
    protected Bulletin bulletinFR = null; //BUlletin in French
    protected Bulletin bulletinDE = null; //BUlletin in German
    private  Zone zone = null; //Zone object that contains the information about provinces and localities
    private static final String PREFERENCES = "it.veneto.arpa.pref"; //Sharedpreferences string
    private static Controller instance = null; //Controller instance
    //XML
    private static final String BULLETIN_IT = "http://www.arpa.veneto.it/previsioni/it/xml/bollettino_widget.xml";
    private static final String BULLETIN_EN = "http://www.arpa.veneto.it/previsioni/en/xml/bollettino_widget.xml";
    private static final String BULLETIN_DE = "http://www.arpa.veneto.it/previsioni/de/xml/bollettino_widget.xml";
    private static final String BULLETIN_FR = "http://www.arpa.veneto.it/previsioni/fr/xml/bollettino_widget.xml";
    //Widget configuration variable
    protected static final String confZone = "confZone";
    protected static final String confCityName = "confCityName";
    protected static final String confColorText = "confColorText";
    protected static final String confColorIcon = "confColorIcon";
    protected static final String confContrast = "confContrast";
    protected static final String confTrasparency = "confTrasparency";
    protected static final String confBackgroundColor = "confBackground";
    protected static final String confLanguage = "confLanguage";
    //Preference selected by the user
    private static final String selectedZone = "city_zone";
    private static final String selectedTextColor = "color_text";
    private static final String selectedIconsColor = "color_icons";
    private static final String selectedContrast = "contrast";
    private static final String selectedBackTrasp = "background_trasp";
    private static final String selectedBackColor = "background_color";
    private static final String selectedLanguage = "language";
    //Cities
    private static final String ZONE = "http://www.arpa.veneto.it/previsioni/it/xml/comuni_app.xml";
    //Service geo
    private boolean geoService = true;

    /**
     * Private controller constructor
     */
    private Controller() {}

    /**
     * Create controller instance if doesn't exist
     * @return controller instance
     */

    public static synchronized Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }

        return instance;
    }

    /**
     * Update widget when provider's onUpdate method is called
     * @param context it's the context of current state of the application
     * @param widgetId
     * @see Update.updateWidgetSimpleInterface(Context context, int widgetId)
     */
    public void updateSimpleWidgetFromProvider(Context context, int widgetId) {
        Update.updateWidgetSimpleInterface(context, widgetId);
    }

    /**
     * Update widget when provider's onUpdate method is called
     * @param context it's the context of current state of the application/object
     * @param widgetId
     * @see Update.updateWidgetDetailedInterface(Context context, int widgetId)
     */
    public void updateWidgetFromProvider(Context context, int widgetId) {
        Update.updateWidgetDetailedInterface(context, widgetId);
    }

    /**
     * Update detailed widget when user click on the bottom
     * @param context it's the context of current state of the application/object
     * @param widgetId
     * @param day the number of the current day from 1 to 5
     * @see Update.updateTop(context, view, widgetId, day); Update.updateBottom(context, view, widgetId, day);
     */
    public void updateWidgetFromClick(Context context, int widgetId, int day) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        verifyBulletin(context, prefs.getString(Controller.confLanguage + widgetId, "IT"));
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.detailed_widget_main);
        Update.updateTop(context, view, widgetId, day);
        Update.updateBottom(context, view, widgetId, day);
        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, view);
    }

    /**
     * Update simple widgets when configuration is finished
     * @param context it's the context of current state of the application/object
     * @param widgetId
     * @param cityName
     * @see Update.updateWidgetSimpleInterface(context, widgetId)
     */
    public void updateSimpleWidgetFromConfiguration(Context context, int widgetId, String cityName) {
        saveWidgetPreferences(context, widgetId, cityName);
        Update.updateWidgetSimpleInterface(context, widgetId);
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putBoolean("configured" + widgetId, true).commit();
    }

    /**
     * Update simple widgets that use geolocation
     * @param context it's the context of current state of the application/object
     * @param widgetId
     * @param cityName
     * @param zoneId
     * @see Update.updateWidgetSimpleInterface(context, widgetId);
     */
    public void updateSimpleWidgetFromConfiguration(Context context, int widgetId, String cityName, String zoneId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        if (!prefs.getBoolean("geoWidgetSavedPref" + widgetId, false)) {
            saveWidgetPreferences(context, widgetId, cityName, zoneId);
            prefs.edit().putBoolean("geoWidgetSavedPref" + widgetId, true).commit();
        }
        else if(!prefs.getString(confCityName + widgetId, "").equals(cityName)) {
            prefs.edit().putString(confCityName + widgetId, cityName).commit();
            prefs.edit().putString(confZone + widgetId, zoneId).commit();
        }

        Update.updateWidgetSimpleInterface(context, widgetId);
        prefs.edit().putBoolean("configured" + widgetId, true).commit();
    }

    /**
     * Update detailed widgets when configuration is finished
     * @param context its the context of current state of the application/object
     * @param widgetId
     * @param cityName
     * @see Update.updateWidgetDetailedInterface(context, widgetId);
     */
    public void updateWidgetFromConfiguration(Context context, int widgetId, String cityName) {
        saveWidgetPreferences(context, widgetId, cityName);
        Update.updateWidgetDetailedInterface(context, widgetId);
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putBoolean("configured" + widgetId, true).commit();
    }

    /**
     * Update detailed widgets that use geolocation
     * @param context its the context of current state of the application/object
     * @param widgetId
     * @param cityName
     * @param zoneId
     * @see Update.updateWidgetDetailedInterface(context, widgetId);
     */
    public void updateWidgetFromConfiguration(Context context, int widgetId, String cityName, String zoneId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        if (!prefs.getBoolean("geoWidgetSavedPref" + widgetId, false)) {
            saveWidgetPreferences(context, widgetId, cityName, zoneId);
            prefs.edit().putBoolean("geoWidgetSavedPref" + widgetId, true).commit();
        }
        else if(!prefs.getString(confCityName + widgetId, "").equals(cityName)) {
            prefs.edit().putString(confCityName + widgetId, cityName).commit();
            prefs.edit().putString(confZone + widgetId, zoneId).commit();
        }

        Update.updateWidgetDetailedInterface(context, widgetId);
        prefs.edit().putBoolean("configured" + widgetId, true).commit();
    }

    /**
     * Save widget preferences in SharedPreferences
     * @param context its the context of current state of the application/object
     * @param widgetId
     * @param cityName
     * @param zoneId
     */
    public void saveWidgetPreferences(Context context, int widgetId, String cityName, String zoneId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences confPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(confZone + widgetId, zoneId).commit();
        prefs.edit().putString(confCityName + widgetId, cityName).commit();
        prefs.edit().putString(confColorText + widgetId, confPrefs.getString(selectedTextColor, "#33B5E5")).commit();
        prefs.edit().putString(confColorIcon + widgetId, confPrefs.getString(selectedIconsColor, "w")).commit();
        prefs.edit().putBoolean(confContrast + widgetId, confPrefs.getBoolean(selectedContrast, false)).commit();
        prefs.edit().putString(confTrasparency + widgetId, confPrefs.getString(selectedBackTrasp, "7F")).commit();
        prefs.edit().putString(confBackgroundColor + widgetId, confPrefs.getString(selectedBackColor, "000000")).commit();
        prefs.edit().putString(confLanguage + widgetId, confPrefs.getString(selectedLanguage, "it")).commit();
    }

    /**
     * Save widget preferences in SharedPreferences
     * @param context its the context of current state of the application/object
     * @param widgetId
     * @param cityName
     */
    public void saveWidgetPreferences(Context context, int widgetId, String cityName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences confPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String id = confPrefs.getString(selectedZone, "0");
        String[] ids = id.split("-");
        prefs.edit().putString(confZone + widgetId, ids[0]).commit();
        prefs.edit().putString(confCityName + widgetId, cityName).commit();
        prefs.edit().putString(confColorText + widgetId, confPrefs.getString(selectedTextColor, "#33B5E5")).commit();
        prefs.edit().putString(confColorIcon + widgetId, confPrefs.getString(selectedIconsColor, "w")).commit();
        prefs.edit().putBoolean(confContrast + widgetId, confPrefs.getBoolean(selectedContrast, false)).commit();
        prefs.edit().putString(confTrasparency + widgetId, confPrefs.getString(selectedBackTrasp, "7F")).commit();
        prefs.edit().putString(confBackgroundColor + widgetId, confPrefs.getString(selectedBackColor, "000000")).commit();
        prefs.edit().putString(confLanguage + widgetId, confPrefs.getString(selectedLanguage, "it")).commit();
    }

    /**
     * Retrieve and return the name of the last cities used
     * @param context its the context of current state of the application/object
     * @return list of last cities name and add geolocation and new city option
     */
    public CharSequence[] getLastCities(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        List<String> lastCities = new ArrayList<String>	();

        if(prefs.contains("lastCity1")) {
            lastCities.add(prefs.getString("lastCity1", ""));
        }

        if(prefs.contains("lastCity2")) {
            lastCities.add(prefs.getString("lastCity2", ""));
        }

        if(prefs.contains("lastCity3")) {
            lastCities.add(prefs.getString("lastCity3", ""));
        }

        lastCities.add(context.getResources().getString(R.string.auto_loc));
        lastCities.add(context.getResources().getString(R.string.add_city));
        return lastCities.toArray(new CharSequence[lastCities.size()]);
    }

    /**
     * Retrieve and return the id of the last cities used
     * @param context its the context of current state of the application/object
     * @return list of last cities id and add add geolocation and new city option id
     */
    public CharSequence[] getLastCitiesId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        List<String> lastCitiesId = new ArrayList<String>	();

        if(prefs.contains("lastCityId1")) {
            lastCitiesId.add(prefs.getString("lastCityId1", ""));
        }

        if(prefs.contains("lastCityId2")) {
            lastCitiesId.add(prefs.getString("lastCityId2", ""));
        }

        if(prefs.contains("lastCityId3")) {
            lastCitiesId.add(prefs.getString("lastCityId3", ""));
        }

        lastCitiesId.add("geo");
        lastCitiesId.add("new");
        return lastCitiesId.toArray(new CharSequence[lastCitiesId.size()]);
    }

    /**
     * Retrieve and return the name of the provinces
     * @param context its the context of current state of the application/object
     * @return list of provinces
     */
    public String[] getProvincesName(Context context) {
        List<String> provincesName = new ArrayList<String>	();

        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

            if(zone != null) {
                provincesName = zone.getProvincesName();
            }
            else if(prefs.contains("zone")) {
                try {
                    zone = (Zone) Serializer.deserialize(prefs.getString("zone", ""));
                }
                catch(IOException e) {
                    e.printStackTrace();
                }

                provincesName = zone.getProvincesName();
            }
            else {
                plist(context);
                provincesName = zone.getProvincesName();
            }

            return provincesName.toArray(new String[provincesName.size()]);
        }
        catch (NullPointerException e) {
            Toast.makeText(context.getApplicationContext(), "Internet connection not detected", Toast.LENGTH_SHORT).show();
            return new String[0];
        }
    }

    /**
     * Retrieve and return the name of a particular province cities
     * @param context its the context of current state of the application/object
     * @param provinceName the name of the province selected
     * @return list of cities
     */
    public String[] getCitiesName(Context context, String provinceName) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        List<String> citiesName = new ArrayList<String>	();

        if(zone != null) {
            Province province = new Province();
            province = zone.getProvince(provinceName);
            citiesName = province.getCitiesName();
        }
        else if(prefs.contains("zone")) {
            try {
                zone = (Zone) Serializer.deserialize(prefs.getString("zone", ""));
                Province province = new Province();
                province = zone.getProvince(provinceName);
                citiesName = province.getCitiesName();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        return citiesName.toArray(new String[citiesName.size()]);
    }

    /**
     * Save the city on sharedPrefrences
     * @param provinceName the city's province name
     * @param cityName the city's name
     * @param context its the context of current state of the application/object
     */
    public void setWidgetCity(Context context, String provinceName, String cityName) {
        String zoneId = zone.getProvince(provinceName).getCity(cityName).getZoneId();
        String cityId = zone.getProvince(provinceName).getCity(cityName).getId();
        saveCityPreference(context, cityName, zoneId, cityId);
    }

    /**
     * Save last cities used by user
     * @param context it's the context of current state of the application/object
     * @param cityName the city's name
     * @param ZoneId zone'id
     * @param cityId
     */
    public void saveCityPreference(Context context, String cityName, String zoneId, String cityId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        if(prefs.contains("lastCity1")) {
            if (prefs.contains("lastCity2")) {
                prefs.edit().putString("lastCity3", prefs.getString("lastCity2", "")).commit();
                prefs.edit().putString("lastCityId3", prefs.getString("lastCityId2", "")).commit();
            }

            prefs.edit().putString("lastCity2", prefs.getString("lastCity1", "")).commit();
            prefs.edit().putString("lastCityId2", prefs.getString("lastCityId1", "")).commit();
        }

        prefs.edit().putString("lastCity1", cityName).commit();
        prefs.edit().putString("lastCityId1", zoneId + "-" + cityId).commit();
    }

    /**
     * Verify if bulletins are update
     * @param context it's the context of current state of the application/object
     * @param language the language of the bulletin you want to trust
     */
    public void verifyBulletin(Context context, String language) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            Bulletin bulletin = null;

            if (language.equals("IT")) {
                bulletin = bulletinIT;
            }
            else if (language.equals("EN")) {
                bulletin = bulletinEN;
            }
            else if (language.equals("FR")) {
                bulletin = bulletinFR;
            }
            else {
                bulletin = bulletinDE;
            }

            if (bulletin == null && !prefs.contains("bulletin" + language)) {
                parseBulletin(context, language);
            }
            else if(bulletin == null && prefs.contains("bulletin" + language)) {
                retrieveBulletin(context, language);
            }
            else if (bulletin != null) {
                String[] date = bulletin.getDate().split("-");
                Calendar cal = new GregorianCalendar(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
                String[] time = bulletin.getTime().split(":");
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                Calendar current = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                if (current.after(cal) && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 13 || current.after(cal) && (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 13 && Calendar.getInstance().get(Calendar.MINUTE) >= 29)) {
                    parseBulletin(context, language);
                }
            }
        }
        catch(StackOverflowError e) {
            e.printStackTrace();
            Toast.makeText(context.getApplicationContext(), "Preference error. Please delete widget data from system option.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Verify if a bulletin is configured
     * @param context it's the context of current state of the application/object
     * @param widgetId
     * @return true if is configured, false otherwise
     */
    public boolean isWidgetConfigured(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        boolean isConfigured = prefs.getBoolean("configured" + widgetId, false);
        return isConfigured;
    }

    /**
     * Download the new bulletin from the xml in the ARPAV site
     * @param context it's the context of current state of the application/object
     * @param language the bulletin language you want to parse
     */
    public void parseBulletin(Context context, String language) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            try {
                ParserBulletin parser = new ParserBulletin();

                if (language.equals("IT")) {
                    bulletinIT = parser.execute(BULLETIN_IT).get();
                }
                else if (language.equals("EN")) {
                    bulletinEN = parser.execute(BULLETIN_EN).get();
                }
                else if (language.equals("FR")) {
                    bulletinFR = parser.execute(BULLETIN_FR).get();
                }
                else {
                    bulletinDE = parser.execute(BULLETIN_DE).get();
                }

                saveBulletin(context, language);
            }
            catch(Error e) {
                e.printStackTrace();
            }
            catch(CancellationException e) {
                e.printStackTrace();
            }
            catch(ExecutionException e) {
                e.printStackTrace();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            //Toast.makeText(context.getApplicationContext(), "Internet connection not detected", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * Save the bulletin object in SharedPreferences if you want to retrieve bulletin without download bulletin again
     * @param context it's the context of current state of the application/object
     * @param language the bulletin language
     */
    public void saveBulletin(Context context, String language) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            String bulletinString = "";

            if (language.equals("IT")) {
                bulletinString = Serializer.serialize(bulletinIT);
            }
            else if (language.equals("EN")) {
                bulletinString = Serializer.serialize(bulletinEN);
            }
            else if (language.equals("FR")) {
                bulletinString = Serializer.serialize(bulletinFR);
            }
            else {
                bulletinString = Serializer.serialize(bulletinDE);
            }

            prefs.edit().putString("bulletin" + language, bulletinString).commit();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get last bulletin saved on SharedPrefrences
     * @param context it's the context of current state of the application/object
     * @param language the bulletin language
     */
    public void retrieveBulletin(Context context, String language) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

            if (language.equals("IT")) {
                bulletinIT =  (Bulletin) Serializer.deserialize(prefs.getString("bulletin" + language, "null"));

                if(bulletinIT == null) {
                    parseBulletin(context, language);
                }
            }
            else if (language.equals("EN")) {
                bulletinEN =  (Bulletin) Serializer.deserialize(prefs.getString("bulletin" + language, "null"));

                if(bulletinEN == null) {
                    parseBulletin(context, language);
                }
            }
            else if (language.equals("FR")) {
                bulletinFR =  (Bulletin) Serializer.deserialize(prefs.getString("bulletin" + language, "null"));

                if(bulletinFR == null) {
                    parseBulletin(context, language);
                }
            }
            else {
                bulletinDE =  (Bulletin) Serializer.deserialize(prefs.getString("bulletin" + language, "null"));

                if(bulletinDE == null) {
                    parseBulletin(context, language);
                }
            }

            verifyBulletin(context, language);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Download the zone information
     * @param context it's the context of current state of the application/object
     */
    public void plist(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting() && zone == null) {
            try {
                SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

                if (!prefs.contains("zone")) {
                    ParserZone parserZone = new ParserZone(context);
                    zone = parserZone.execute(ZONE).get();
                    String zoneString = Serializer.serialize(zone);
                    prefs.edit().putString("zone", zoneString).commit();
                }
            }
            catch(Error e) {
                e.printStackTrace();
            }
            catch(CancellationException e) {
                e.printStackTrace();
            }
            catch(ExecutionException e) {
                e.printStackTrace();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the alarm to update bulletin every day at 13.30
     * @param context it's the context of current state of the application/object
     */
    public void updateBulletinAlarm(Context context, String typeWidget) {
        Calendar cur_cal = new GregorianCalendar();
        int day = 0;

        if (cur_cal.get(Calendar.HOUR_OF_DAY) > 13) {
            day = 1;
        }
        else if (cur_cal.get(Calendar.HOUR_OF_DAY) == 13 && cur_cal.get(Calendar.MINUTE) >= 30) {
            day = 1;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 13);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE) + day);
        Intent intent;

        if (typeWidget.equals("d")) {
            intent = new Intent(context, it.veneto.arpa.view.DetailedWidgetProvider.class);
        }
        else {
            intent = new Intent(context, it.veneto.arpa.view.SimpleWidgetProvider.class);
        }

        intent.setAction("updateBulletinAlarm");
        PendingIntent pintent = PendingIntent.getBroadcast(context, 1, intent, 0);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC, cal.getTimeInMillis(), pintent);
    }


    /**
     * Set alarm to set the detailed widget on the new day at 00:00
     * @param context it's the context of current state of the application/object
     */
    public void updateWidgetAlarm(Context context, String typeWidget) {
        Calendar cur_cal = new GregorianCalendar();
        int day = 0;

        if (cur_cal.get(Calendar.HOUR_OF_DAY) > 00) {
            day = 1;
        }
        else if (cur_cal.get(Calendar.HOUR_OF_DAY) == 00 && cur_cal.get(Calendar.MINUTE) >= 01) {
            day = 1;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 01);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE) + day);
        Intent intent;

        if (typeWidget.equals("d")) {
            intent = new Intent(context, it.veneto.arpa.view.DetailedWidgetProvider.class);
        }
        else {
            intent = new Intent(context, it.veneto.arpa.view.SimpleWidgetProvider.class);
        }

        intent.setAction("updateMidnight");
        PendingIntent pintent = PendingIntent.getBroadcast(context, 2, intent, 0);
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC, cal.getTimeInMillis(), pintent);
    }

    /**
     * Set the locality for geolocation widgets
     * @param context it's the context of current state of the application/object
     * @param location location find from LocationManager
     * @param widgetId
     */

    public synchronized void geoLocation(Context context, String lat, String lon, String widgetId, String widgetType) {
        ParserGeoLocal task = new ParserGeoLocal(context, lat, lon);

        try {
            String result = task.execute().get();
            String[] res = result.split("/");

            if (res.length == 2) {
                if (widgetType.equals("d")) {
                    updateWidgetFromConfiguration(context.getApplicationContext(), Integer.parseInt(widgetId), res[0], res[1]);
                }
                else if(widgetType.equals("s")) {
                    updateSimpleWidgetFromConfiguration(context.getApplicationContext(), Integer.parseInt(widgetId), res[0], res[1]);
                }
            }
            else {
                //Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Error e) {
            e.printStackTrace();
        }
        catch(CancellationException e) {
            e.printStackTrace();
        }
        catch(ExecutionException e) {
            e.printStackTrace();
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Verify if a widget use geolocation
     * @param context it's the context of current state of the application/object
     * @param widgetId
     * @return true uf the widget is geolocation, false otherwise
     */
    public boolean isWidgetGeoLocal(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return prefs.getBoolean("geolocal" + widgetId, false);
    }

    /**
     * Save that a particular widget use geolocation
     * @param context it's the context of current state of the application/object
     * @param widgetId
     */
    public void saveWidgetGeoLocal(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putBoolean("geolocal" + widgetId, true).commit();
    }

    /**
     * Get the zone information for widgets that use geolocation
     * @param context it's the context of current state of the application/object
     * @param city
     * @param province
     * @return the  string that contains the zone id find
     */
    public String verifyGeoLocal(Context context, String city, String province) {
        String s = "";
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        try {
            if(zone != null) {
                s = zone.getProvince(province).getCity(city).getZoneId();
            }
            else if(prefs.contains("zone")) {
                try {
                    zone = (Zone) Serializer.deserialize(prefs.getString("zone", ""));
                }
                catch(IOException e) {
                    e.printStackTrace();
                }

                s = zone.getProvince(province).getCity(city).getZoneId();
            }
            else {
                plist(context);
                s = zone.getProvince(province).getCity(city).getZoneId();
            }

            return s;
        }
        catch (NullPointerException e) {
            return s;
        }
    }

    /**
     * Set the value to stop geolocation service
     * @param condition true to stop service, false otherwise
     */
    public void stopService(boolean condition) {
        geoService = condition;
    }

    /**
     * Verify if geolocation service must stop
     * @return true for stop servie, false otherwise
     */
    public boolean stopService() {
        return geoService;
    }
}