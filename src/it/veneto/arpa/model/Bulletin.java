package it.veneto.arpa.model;

import it.veneto.arpa.model.Meteogram;
import java.util.HashMap;
import java.io.Serializable;
/**
 * Class that represents the daily bulletin
 * @author Luca
 *
 */
public class Bulletin implements Serializable {
    private static final long serialVersionUID = 7526471155622776147L; //Serializable id
    private HashMap<String, Meteogram> meteograms; //Bulletin Meteograms array
    private String date; //Bulletin publication date
    private String time; //Bulletin publication time

    public Bulletin(String date, HashMap<String, Meteogram> meteograms ) {
        this.meteograms = meteograms;
        this.date = date;
    }

    public Bulletin(String date, String time) {
        this.meteograms = new HashMap<String, Meteogram>();
        this.date = date;
        this.time = time;
    }

    public void insertMeteogram(String id, Meteogram m) {
        meteograms.put(id, m);
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    /**
     * Get the sky description of particular zone and day
     * @param zoneId the id zone
     * @param day the number of the day
     * @return string that contains the sky description
     */
    public String getSkyDescription(String zoneId, int day) {
        Day d = meteograms.get(zoneId).getDay(day);
        return d.getSkyDescription();
    }

    /**
     * Get the name of the sky image of particular zone and day
     * @param zoneId the id zone
     * @param day the number of the day
     * @return string that contains the sky image
     */
    public String getSkyImg(String zoneId, int day) {
        Day d = meteograms.get(zoneId).getDay(day);
        String s = d.getSkyImg();
        return s;
    }

    /**
     * Get the rain description of particular zone and day
     * @param zoneId the id zone
     * @param day the number of the day
     * @return string that contains the rain description
     */
    public String getRainDescription(String zoneId, int day) {
        Day d = meteograms.get(zoneId).getDay(day);
        return d.getRainDescription();
    }

    /**
     * Get the temperature description for the top of widgets
     * @param zoneId the id zone
     * @param day the number of the day
     * @return string that contains the temperature description
     */
    public String getTemp(String zoneId, int day) {
        Day d = meteograms.get(zoneId).getDay(day);
        String min = d.getTempMin("2000");
        String max = d.getTempMax("2000");

        if (!min.equals("")) {
            min = "min " + min + "¡";
        }

        if (!max.equals("")) {
            if (!min.equals("")) {
                max = "-max " + max + "¡";
            }
            else {
                max = "max " + max + "¡";
            }
        }

        return min + max;
    }

    public Meteogram getMeteogram(String zoneId) {
        return meteograms.get(zoneId);
    }
}
