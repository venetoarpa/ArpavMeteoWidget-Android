package it.veneto.arpa.view;

import it.veneto.arpa.controller.Controller;
import it.veneto.arpa.controller.GeolocationService;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.content.ComponentName;
import android.util.Log;

/**
 * Simple widget provider
 * @author Luca
 *
 */
public class SimpleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * @see Android AppWidgetProvider onUpdate
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final int N = appWidgetIds.length;

        for (int i = 0; i < N; i++) {
            int widgetId = appWidgetIds[i];

            if (Controller.getInstance().isWidgetConfigured(context, widgetId)) {
                Controller.getInstance().updateSimpleWidgetFromProvider(context, widgetId);

                if(Controller.getInstance().isWidgetGeoLocal(context, widgetId)) {
                    Intent intent = new Intent(context, GeolocationService.class );
                    intent.putExtra("widgetType", "s");
                    intent.putExtra("widgetId", widgetId);
                    context.startService(intent);
                }
            }
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    /**
     * @see Android AppWidgetProvider onReceive
     */
    @Override
    public void onReceive (Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals("openARPAV")) {
            try {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("it.redturtle.mobile.apparpav");
                context.startActivity(launchIntent);
            }
            catch(NullPointerException e ) {
                Toast.makeText(context.getApplicationContext(), "ARPAV Meteo not installed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else if(intent.getAction().equals("updateBulletinAlarm")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName comp = new ComponentName(context, it.veneto.arpa.view.SimpleWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(comp);
            onUpdate(context, appWidgetManager, appWidgetIds);
            Controller.getInstance().updateBulletinAlarm(context, "s");
        }
        else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED") || intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")) {
            Controller.getInstance().updateBulletinAlarm(context, "s");
            Controller.getInstance().updateWidgetAlarm(context, "s");
        }
        else if (intent.getAction().equals("updateMidnight")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName comp = new ComponentName(context, it.veneto.arpa.view.SimpleWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(comp);
            onUpdate(context, appWidgetManager, appWidgetIds);
            Controller.getInstance().updateWidgetAlarm(context, "s");
        }
        else if (intent.getAction().equals("updateInternet")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName comp = new ComponentName(context, it.veneto.arpa.view.SimpleWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(comp);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
        else if (intent.getAction().equals("android.appwidget.action.APPWIDGET_DELETED")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName comp = new ComponentName(context, it.veneto.arpa.view.SimpleWidgetProvider.class);
            ComponentName compDet = new ComponentName(context, it.veneto.arpa.view.DetailedWidgetProvider.class);
            int[] appWidgetIdS = appWidgetManager.getAppWidgetIds(comp);
            int[] appWidgetIdD = appWidgetManager.getAppWidgetIds(compDet);
            int[] appWidgetIds = new int[appWidgetIdS.length + appWidgetIdD.length];
            System.arraycopy(appWidgetIdS, 0, appWidgetIds, 0, appWidgetIdS.length);
            System.arraycopy(appWidgetIdD, 0, appWidgetIds, appWidgetIdS.length, appWidgetIdD.length);
            final int N = appWidgetIds.length;
            boolean geo = true;

            for (int i = 0; i < N && geo; i++) {
                geo = !(Controller.getInstance().isWidgetGeoLocal(context, appWidgetIds[i]));
            }

            if (geo) {
                Controller.getInstance().stopService(true);
            }
        }
        else if (intent.getAction().equals("it.veneto.arpa.geo")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName comp = new ComponentName(context, it.veneto.arpa.view.SimpleWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(comp);
            final int N = appWidgetIds.length;

            for (int i = 0; i < N; i++) {
                if (Controller.getInstance().isWidgetGeoLocal(context, appWidgetIds[i])) {
                    Controller.getInstance().geoLocation(context, intent.getStringExtra("LAT"), intent.getStringExtra("LON"), appWidgetIds[i] + "", "s");
                }
            }
        }
        else if (intent.getAction().equals("refresh")) {
            Log.i("REFRESH", "SIMPLE-PROVIDER");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName comp = new ComponentName(context, it.veneto.arpa.view.SimpleWidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(comp);
            final int N = appWidgetIds.length;

            for (int i = 0; i < N; i++) {
                int widgetIdR = appWidgetIds[i];

                if (Controller.getInstance().isWidgetConfigured(context, widgetIdR)) {
                    Controller.getInstance().updateSimpleWidgetFromProvider(context, widgetIdR);

                    if(Controller.getInstance().isWidgetGeoLocal(context, widgetIdR)) {
                        Intent intentR = new Intent(context, GeolocationService.class );
                        intentR.putExtra("widgetType", "s");
                        intentR.putExtra("widgetId", widgetIdR);
                        context.startService(intentR);
                    }
                }
            }
        }
    }
}
