package it.veneto.arpa.model;

import java.io.Serializable;

/**
 * Class that represent all the temperature of a particular day
 * @author Luca
 *
 */
public class Temperature implements Serializable {
    private static final long serialVersionUID = 7526471155622776147L; //
    private String mt; //
    private String min; //
    private String max;  //

    public Temperature() {
    }

    public void setMt(String mt) {
        this.mt = mt;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getTempMax() {
        return max;
    }

    public String getTempMin() {
        return min;
    }

    public String getMt() {
        return mt;
    }
}
