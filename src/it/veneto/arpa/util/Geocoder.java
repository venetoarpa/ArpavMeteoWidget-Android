package it.veneto.arpa.util;

import it.veneto.arpa.controller.Controller;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

import android.content.Context;

/**
 * Class used to parse json from openstreetmap for geolocation service
 * @author Luca
 *
 */
public class Geocoder {

    /**
     * Find Location name from latitude and logitude
     * @param lat latitude
     * @param lot longitude
     * @param context
     * @return string that contains "cityName"+"/"+"cityZone"
     */
    public static String reverseGeocode(String lat, String lon, Context context) {
        String localityName = "";
        HttpURLConnection connection = null;
        URL serverAddress = null;

        try {
            serverAddress = new URL("http://nominatim.openstreetmap.org/reverse?format=json&lat="
                                    + lat
                                    + "&lon=" + lon
                                    + "&accept-language=it");
            connection = null;
            connection = (HttpURLConnection)serverAddress.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setReadTimeout(10000);
            connection.connect();

            try {
                BufferedReader isr = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = isr.readLine()) != null) {
                    sb.append(line + "\n");
                }

                isr.close();
                String json = sb.toString();
                JSONObject jsonObject = new JSONObject(json);
                JSONObject city = jsonObject.getJSONObject("address");
                localityName = city.getString("city");
                String province = city.getString("county");
                province = extendedProvinceName(province);
                String zoneId = Controller.getInstance().verifyGeoLocal(context, localityName, province);
                localityName = localityName + "/" + zoneId;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        connection.disconnect();
        return localityName;
    }

    /**
     * Find province name from province abbreviation
     * @param province
     * @return
     */
    public static String extendedProvinceName(String province) {
        String prov = "";

        if (province.equals("VE")) {
            prov = "Venezia";
        }
        else if (province.equals("TV")) {
            prov = "Treviso";
        }
        else if (province.equals("PD")) {
            prov = "Padova";
        }
        else if (province.equals("BL")) {
            prov = "Belluno";
        }
        else if (province.equals("RO")) {
            prov = "Rovigo";
        }
        else if (province.equals("VR")) {
            prov = "Verona";
        }
        else if (province.equals("VI")) {
            prov = "Vicenza";
        }

        return prov;
    }
}