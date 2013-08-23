package it.veneto.arpa.model;

import java.util.ArrayList;
import java.io.Serializable;

import it.veneto.arpa.model.Temperature;

/**
 * Class that represent all the single days of a particular meteogram
 * @author Luca
 *
 */
public class Day implements Serializable {
    private ArrayList<Temperature> temp; //Temperature value array
    private String date = ""; //date string
    private String time = ""; //time string
    private String skyImg = ""; //sky image string
    private String skyDescription = ""; //sky description
    private String rainDescription = ""; //rain description
    private String rainPerc = ""; //rain percentage
    private static final long serialVersionUID = 7526471155622776147L; //serializable id

    public Day() {
        temp = new ArrayList<Temperature>();
    }

    public Day(ArrayList<Temperature> temp, String skyImg, String skyDescription, String rainDescription, String rainPerc) {
        this.temp = temp;
        this.skyDescription = skyDescription;
        this.skyImg = skyImg;
        this.rainDescription = rainDescription;
        this.rainPerc = rainPerc;
    }

    public void addTemp(Temperature t) {
        temp.add(t);
    }

    public void setSkyDescription(String skyDescription) {
        this.skyDescription = skyDescription;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        String s = date;
        s = s.substring(s.length() - 2, s.length());
        return s;
    }

    public String getTime(String language) {
        String mor = selectMorString(language);
        String aft = selectAftString(language);

        if (time.equals("pm")) {
            return aft;
        }
        else if(time.equals("am")) {
            return mor;
        }
        else {
            return "";
        }
    }

    /**
     * Select "morning" abbreviation in a particular language
     * @param language
     * @return string that contains the abbreviation
     */
    public String selectMorString(String language) {
        if (language.equals("IT")) {
            return " MAT";
        }
        else if (language.equals("EN")) {
            return " MOR";
        }
        else if (language.equals("FR")) {
            return " MAT";
        }
        else {
            return " MOR";
        }
    }

    /**
     * Select "afternoon" abbreviation in a particular language
     * @param language
     * @return string that contains the abbreviation
     */
    public String selectAftString(String language) {
        if (language.equals("IT")) {
            return " POM";
        }
        else if (language.equals("EN")) {
            return " AFT";
        }
        else if (language.equals("FR")) {
            return " APR";
        }
        else {
            return " NACH";
        }
    }

    public void setSkyImg(String skyImg) {
        this.skyImg = skyImg;
    }

    public void setRainDescription(String rainDescription) {
        this.rainDescription = rainDescription;
    }

    public void setrainPerc(String rainPerc) {
        this.rainPerc = rainPerc;
    }

    /**
     * Get the temperature max for top interface
     * @param mt
     * @return string contains temp max
     */
    public String getTempMax(String mt) {
        String tempMax = "";

        for (Temperature i : temp) {
            if (i.getMt().equals(mt) || i.getMt().equals("")) {
                tempMax = i.getTempMax();
            }
        }

        return tempMax;
    }

    /**
     * Get the temperature max for the bottom days interface
     * @param mt
     * @return string contains temp max
     */
    public String getTempMaxDay(String mt) {
        String tempMax = "";
        tempMax = getTempMax(mt);

        if (tempMax.contains("/")) {
            String[] temps = tempMax.split("/");
            return temps[1] + "¡";
        }
        else if (!tempMax.equals("")) {
            return tempMax + "¡";
        }
        else {
            return "-";
        }
    }

    /**
     * Get the temperature min for top interface
     * @param mt
     * @return string contains temp min
     */
    public String getTempMin(String mt) {
        String tempMin = "";

        for (Temperature i : temp) {
            if (i.getMt().equals(mt) || i.getMt().equals("")) {
                tempMin = i.getTempMin();
            }
        }

        return tempMin;
    }

    /**
     * Get the temperature min for the bottom days interface
     * @param mt
     * @return string contains temp min
     */
    public String getTempMinDay(String mt) {
        String tempMin = "";
        tempMin = getTempMin(mt);

        if (tempMin.contains("/")) {
            String[] temps = tempMin.split("/");
            return temps[0] + "¡";
        }
        else if (!tempMin.equals("")) {
            return tempMin + "¡";
        }
        else {
            return "-";
        }
    }

    public String getSkyDescription() {
        if (skyDescription.equals("")) {
            return "-";
        }
        else {
            return skyDescription;
        }
    }

    public String getSkyImg() {
        String s = skyImg;
        s = s.substring(0, s.length() - 4);
        return s;
    }

    public String getRainPerc() {
        if (!rainPerc.equals("")) {
            return " (" + rainPerc + "): ";
        }
        else {
            return ": ";
        }
    }

    public String getRainDescription() {
        if (rainDescription.equals("")) {
            return "-";
        }
        else {
            return rainDescription;
        }
    }

    public String toString() {
        String s = "Day " + skyImg + " " + skyDescription + "" + rainDescription;
        return s;
    }
}
