package it.veneto.arpa.controller;

import java.util.Calendar;
import java.util.GregorianCalendar;

import it.veneto.arpa.R;
import it.veneto.arpa.model.Meteogram;
import it.veneto.arpa.model.Bulletin;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.Toast;
import android.util.Log;
import android.widget.RemoteViews;
import android.app.Activity;
import android.content.Intent;
import android.app.PendingIntent;


/**
 * Update class provides methods to update widgets interface, information bulletins
 * and information zone
 * @author Luca
 */
public class Update extends Activity {
    private static final String PREFERENCES = "it.veneto.arpa.pref";

    /**
     * Updates simple widget visual interface
     * @param context it's the context of current state of the application
     * @param widgetId
     */
    public static void updateWidgetSimpleInterface(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Controller.getInstance().verifyBulletin(context, prefs.getString(Controller.confLanguage + widgetId, "IT"));
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.simple_widget_main);

        try {
            Calendar cur_cal = new GregorianCalendar();
            int dayTime = 1;

            if ( (cur_cal.get(Calendar.HOUR_OF_DAY) >= 00 && cur_cal.get(Calendar.HOUR_OF_DAY) < 13) || (cur_cal.get(Calendar.HOUR_OF_DAY) == 13 && cur_cal.get(Calendar.MINUTE) <= 2)) {
                dayTime = 2;
            }

            //Click
            Intent intent = new Intent(context, it.veneto.arpa.view.SimpleWidgetProvider.class);
            intent.setAction("refresh");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            view.setOnClickPendingIntent(R.id.refresh, pendingIntent);
            updateTop(context, view, widgetId, dayTime);
            AppWidgetManager.getInstance(context).updateAppWidget(widgetId, view);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            errorUpdate(context, view, widgetId, "s");
        }
    }

    /**
     * Updates detailed widget visual interface
     * @param context it's the context of current state of the application
     * @param widgetId
     */
    public static void updateWidgetDetailedInterface(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Controller.getInstance().verifyBulletin(context, prefs.getString(Controller.confLanguage + widgetId, "IT"));
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.detailed_widget_main);

        try {
            Calendar cur_cal = new GregorianCalendar();
            int dayTime = 1;

            if ((cur_cal.get(Calendar.HOUR_OF_DAY) >= 00 && cur_cal.get(Calendar.HOUR_OF_DAY) < 13) || (cur_cal.get(Calendar.HOUR_OF_DAY) == 13 && cur_cal.get(Calendar.MINUTE) <= 29) ) {
                dayTime = 2;
            }

            //Click
            Intent intent = new Intent(context, it.veneto.arpa.view.DetailedWidgetProvider.class);
            intent.setAction("refresh");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            view.setOnClickPendingIntent(R.id.refresh, pendingIntent);
            updateTop(context, view, widgetId, dayTime);
            updateBottom(context, view, widgetId, dayTime);
            AppWidgetManager.getInstance(context).updateAppWidget(widgetId, view);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            errorUpdate(context, view, widgetId, "d");
        }
    }

    /**
     * Update simple widget or detailed widget top interface
     * @param context it's the context of current state of the application
     * @param view the widget remoteView
     * @param widgetId
     * @param dayTime the current day from 1 to 5
     */

    public static synchronized void updateTop(Context context, RemoteViews view, int widgetId, int dayTime) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        //Language
        String language =  prefs.getString(Controller.confLanguage + widgetId, "IT");
        Bulletin bulletin = findBulletinLanguage(language);
        String skyString = selectSkyString(language);
        String rainString = selectRainString(language);
        //Background
        String back = "#" + prefs.getString(Controller.confTrasparency + widgetId, "49") + prefs.getString(Controller.confBackgroundColor + widgetId, "000000");
        view.setInt(R.id.main_layout, "setBackgroundColor", Color.parseColor(back));
        String backtext = "#000000";
        String ref = "b";

        if(prefs.getString(Controller.confBackgroundColor + widgetId, "000000").equals("000000")) {
            backtext = "#FFFFFF";
            ref = "w";
        }

        //Arpav
        view.setTextColor(R.id.arpa, Color.parseColor(prefs.getString(Controller.confColorText + widgetId, "#33B5E5")));
        //Refresh
        String pathR = "it.veneto.arpa:drawable/refresh" + ref;
        int idR = context.getResources().getIdentifier(pathR, null, null);
        view.setImageViewResource(R.id.refresh, idR);
        //Zone id
        String zoneId = prefs.getString(Controller.confZone + widgetId, "0");
        //City name
        view.setTextViewText(R.id.city_name, prefs.getString(Controller.confCityName + widgetId, ""));
        view.setTextColor(R.id.city_name, Color.parseColor(prefs.getString(Controller.confColorText + widgetId, "#33B5E5")));
        //Sky Description
        String skyDesc = skyString + bulletin.getSkyDescription(zoneId, dayTime);

        if (skyDesc.length() > 58) {
            skyDesc = skyDesc.substring(0, 55) + "...";
        }

        view.setTextViewText(R.id.sky_description, skyDesc);
        view.setTextColor(R.id.sky_description, Color.parseColor(backtext));
        //Rain Description
        String rainDesc = rainString + bulletin.getMeteogram(zoneId).getDay(dayTime).getRainPerc() + bulletin.getRainDescription(zoneId, dayTime);

        if (rainDesc.length() > 58) {
            rainDesc = rainDesc.substring(0, 55) + "...";
        }

        view.setTextViewText(R.id.rain_description, rainDesc);
        view.setTextColor(R.id.rain_description, Color.parseColor(backtext));
        //Temperature description
        view.setTextViewText(R.id.temp , bulletin.getTemp(zoneId, dayTime));
        view.setTextColor(R.id.temp, Color.parseColor(prefs.getString(Controller.confColorText + widgetId, "#33B5E5")));
        //img
        String path = "it.veneto.arpa:drawable/" + bulletin.getSkyImg(zoneId, dayTime) + prefs.getString(Controller.confColorIcon + widgetId, "w");
        int id = context.getResources().getIdentifier(path, null, null);
        view.setImageViewResource(R.id.sky_img, id);
        //click
        Intent intent = new Intent(context, it.veneto.arpa.view.DetailedWidgetProvider.class);
        intent.setAction("openARPAV");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        view.setOnClickPendingIntent(R.id.top_layout, pendingIntent);

        //Contrast
        if (prefs.getBoolean(Controller.confContrast + widgetId, false)) {
            path = "it.veneto.arpa:drawable/" + bulletin.getSkyImg(zoneId, dayTime) + "b";
            id = context.getResources().getIdentifier(path, null, null);
            view.setImageViewResource(R.id.sky_img, id);
            updateTopH(view);
        }
    }

    /**
     * Update top id widget use high contrast
     * @param view
     */
    public static void updateTopH(RemoteViews view) {
        String colorText = "#000000";
        view.setInt(R.id.main_layout, "setBackgroundColor", Color.parseColor("#FFFFFF"));
        view.setInt(R.id.separator, "setBackgroundColor", Color.parseColor(colorText));
        //Arpa
        view.setTextColor(R.id.arpa, Color.parseColor(colorText));
        //City
        view.setTextColor(R.id.city_name, Color.parseColor(colorText));
        //temp
        view.setTextColor(R.id.temp, Color.parseColor(colorText));
        //Rain
        view.setTextColor(R.id.rain_description, Color.parseColor(colorText));
        //Sky
        view.setTextColor(R.id.sky_description, Color.parseColor(colorText));
    }

    /**
     * Update detailed widget bottom interface
     * @param context it's the context of current state of the application
     * @param view
     * @param widgetId
     * @param dayTime the current day from 1 to 5
     */
    public static void updateBottom(Context context, RemoteViews view, int widgetId, int dayTime) {
        view.removeAllViews(R.id.bottom_layout);

        //Days
        for (int i = 1; i <= 5; i++)
            if (i == dayTime) {
                updateBottomDays(context, view, widgetId, true, i);
            }
            else {
                updateBottomDays(context, view, widgetId, false, i);
            }
    }

    /**
     * Update every single day of the detailed widget
     * @param context it's the context of current state of the application
     * @param view
     * @param widgetId
     * @param selected true if the it's the current day
     * @param day the current day from 1 to 5
     */
    public static void updateBottomDays(Context context, RemoteViews view, int widgetId, boolean selected, int day) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        //Separator
        String separator = "#80000000";

        if(prefs.getString(Controller.confBackgroundColor + widgetId, "000000").equals("000000")) {
            separator = "#80FFFFFF";
        }

        view.setInt(R.id.separator, "setBackgroundColor", Color.parseColor(separator));
        //Language
        String language =  prefs.getString(Controller.confLanguage + widgetId, "IT");
        Bulletin bulletin = findBulletinLanguage(language);
        String zoneId = prefs.getString(Controller.confZone + widgetId, "0");
        //Background
        String backtext = "#000000";

        if(prefs.getString(Controller.confBackgroundColor + widgetId, "000000").equals("000000")) {
            backtext = "#FFFFFF";
        }

        Meteogram meteogram = bulletin.getMeteogram(zoneId);
        //days
        RemoteViews newView = new RemoteViews(context.getPackageName(), R.layout.detailed_widget_days);
        newView.setTextViewText(R.id.day_date, meteogram.getDay(day).getDate() + meteogram.getDay(day).getTime(language));
        newView.setTextColor(R.id.day_date, Color.parseColor(prefs.getString(Controller.confColorText + widgetId, "#33B5E5")));
        newView.setTextViewText(R.id.day_temp, meteogram.getDay(day).getTempMinDay("2000") + "  " + meteogram.getDay(day).getTempMaxDay("2000"));
        newView.setTextColor(R.id.day_temp, Color.parseColor(backtext));

        //Selected
        if (selected) {
            newView.setInt(R.id.day_selected, "setBackgroundColor", Color.parseColor(prefs.getString(Controller.confColorText + widgetId, "#33B5E5")));
        }

        //img
        String path = "it.veneto.arpa:drawable/" + bulletin.getSkyImg(zoneId, day) + prefs.getString(Controller.confColorIcon + widgetId, "w");
        int id = context.getResources().getIdentifier(path, null, null);
        newView.setImageViewResource(R.id.day_img, id);
        //Click
        Intent intent = new Intent(context, it.veneto.arpa.view.DetailedWidgetProvider.class);
        intent.setAction("clickOnDay" + widgetId + day);
        intent.putExtra("dayNumber", day);
        intent.putExtra("widgetId", widgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        newView.setOnClickPendingIntent(R.id.layout_click1, pendingIntent);

        //Contrast
        if (prefs.getBoolean(Controller.confContrast + widgetId, false)) {
            view.setInt(R.id.separator, "setBackgroundColor", Color.parseColor("#000000"));
            path = "it.veneto.arpa:drawable/" + bulletin.getSkyImg(zoneId, day) + "b";
            id = context.getResources().getIdentifier(path, null, null);
            newView.setImageViewResource(R.id.day_img, id);

            if (selected) {
                newView.setInt(R.id.day_selected, "setBackgroundColor", Color.parseColor("#000000"));
            }

            updateBottomDaysH(newView);
        }

        view.addView(R.id.bottom_layout, newView);
    }

    /**
     * Update detailed widget bottom interface if it use high contrast
     * @param view
     */
    public static void updateBottomDaysH(RemoteViews view) {
        String colorText = "#000000";
        view.setTextColor(R.id.day_date, Color.parseColor(colorText));
        view.setTextColor(R.id.day_temp, Color.parseColor(colorText));
    }

    /**
     * Select the initial string for the sky description
     * @param language
     * @return string contains "sky" in the selected language
     */
    public static String selectSkyString(String language) {
        if (language.equals("IT")) {
            return "Cielo: ";
        }
        else if (language.equals("EN")) {
            return "Sky: ";
        }
        else if (language.equals("FR")) {
            return "Ciel: ";
        }
        else {
            return "Himmel: ";
        }
    }

    /**
     * Select the initial string for the rain description
     * @param language
     * @return string contains "rain" in the selected language
     */
    public static String selectRainString(String language) {
        if (language.equals("IT")) {
            return "Piogge";
        }
        else if (language.equals("EN")) {
            return "Rain";
        }
        else if (language.equals("FR")) {
            return "Douche";
        }
        else {
            return "Dusche";
        }
    }

    /**
     * Find the bulletin from the language
     * @param language
     * @return the bulletin object
     */
    public static Bulletin findBulletinLanguage(String language) {
        Bulletin bulletin = null;

        if (language.equals("IT")) {
            bulletin = Controller.getInstance().bulletinIT;
        }
        else if (language.equals("EN")) {
            bulletin = Controller.getInstance().bulletinEN;
        }
        else if (language.equals("FR")) {
            bulletin = Controller.getInstance().bulletinFR;
        }
        else {
            bulletin = Controller.getInstance().bulletinDE;
        }

        return bulletin;
    }

    /**
     * Set the widget interface if the bulletin contains some errors
     * @param context
     * @param view
     * @param widgetId
     * @param type "d" if the widget is detailed, "s" if the widget is simple
     */
    public static void errorUpdate(Context context, RemoteViews view, int widgetId, String type) {
        String colorText = "#000000";
        Toast.makeText(context.getApplicationContext(), "Weather data is incorrect", Toast.LENGTH_SHORT).show();
        view.setInt(R.id.main_layout, "setBackgroundColor", Color.parseColor("#FFFFFF"));
        view.setTextViewText(R.id.sky_description, "No internet connection");
        view.setTextViewText(R.id.rain_description, "Click to update");
        view.setTextColor(R.id.city_name, Color.parseColor(colorText));
        view.setTextColor(R.id.temp, Color.parseColor(colorText));
        view.setTextColor(R.id.rain_description, Color.parseColor(colorText));
        view.setTextColor(R.id.sky_description, Color.parseColor(colorText));
        Intent intent;

        if (type.equals("d")) {
            intent = new Intent(context, it.veneto.arpa.view.DetailedWidgetProvider.class);
        }
        else {
            intent = new Intent(context, it.veneto.arpa.view.SimpleWidgetProvider.class);
        }

        intent.setAction("updateInternet");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        view.setOnClickPendingIntent(R.id.top_layout, pendingIntent);
        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, view);
    }
}
