package it.veneto.arpa.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import it.veneto.arpa.model.City;

/**
 * Class that represent all the provinces in Veneto
 * @author Luca
 *
 */
public class Province implements Serializable {
    private static final long serialVersionUID = 7526471155622776147L;//Serializable id
    private String name; //Province name
    private ArrayList<City>  cities; //Province cities

    public Province() {
        cities = new ArrayList<City>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addCity(City city) {
        cities.add(city);
    }

    /**
     * Get all province cities name
     * @return a list of the province cities name
     */
    public List<String> getCitiesName() {
        List<String> citiesName = new ArrayList<String>	();

        for (City i : cities) {
            citiesName.add(i.getName());
        }

        return citiesName;
    }

    /**
     * Get a particular city object
     * @param cityName
     * @return the city object
     */
    public City getCity(String cityName) {
        City city = new City();

        for (City i : cities) {
            if (i.getName().equals(cityName)) {
                city = i;
            }
        }

        return city;
    }
}
