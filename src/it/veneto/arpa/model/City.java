package it.veneto.arpa.model;

import java.io.Serializable;

/**
 * Class that represents all the Veneto municipalities
 * @author Luca
 *
 */
public class City implements Serializable {
    private static final long serialVersionUID = 7526471155622776147L; //Serializable id
    private String id; //City id
    private String name; //City name
    private String zoneId; //City zone

    public City(String id, String name, String zoneId) {
        this.id = id;
        this.name = name;
        this.zoneId = zoneId;
    }

    public City(String id) {
        this.id = id;
    }

    public City() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getZoneId() {
        return zoneId;
    }

    public City setZoneId(String id) {
        this.zoneId = id;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCityId(String cityId) {
        this.id = cityId;
    }

    public int compareTo(Object arg0) {
        return 0;
    }

}
