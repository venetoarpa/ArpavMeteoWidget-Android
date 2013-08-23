package it.veneto.arpa.model;

import it.veneto.arpa.model.Day;
import java.util.ArrayList;
import java.io.Serializable;
/**
 * Class that represent all meteograms contained in the bulletin
 * @author Luca
 *
 */
public class Meteogram implements Serializable {
    private static final long serialVersionUID = 7526471155622776147L; //Serializable id
    private ArrayList<Day> days; //Meteogram day array
    private String zoneId; //Meteogram zone id

    public Meteogram(String id, ArrayList<Day> days) {
        this.zoneId = id;
        this.days = days;
    }

    public Meteogram(String id) {
        this.zoneId = id;
        this.days = new ArrayList<Day>();
    }

    public void insertDay(Day d) {
        this.days.add(d);
    }

    public String getZoneId() {
        return zoneId;
    }

    public Day getDay(int i) {
        return days.get(i - 1);
    }
}
