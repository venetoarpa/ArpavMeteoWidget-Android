package it.veneto.arpa.util.parser;

import it.veneto.arpa.util.Geocoder;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Parsing geolocation
 * @author Luca
 *
 */
public class ParserGeoLocal extends AsyncTask <Void, Void, String> {
    protected Context applicationContext;
    private String lat;
    private String lon;

    public ParserGeoLocal(Context context, String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
        this.applicationContext = context;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... params) {
        String localityName = "";
        localityName = Geocoder.reverseGeocode(lat, lon, applicationContext);
        return localityName;
    }

    @Override
    protected void onPostExecute(String result) {
    }
}
