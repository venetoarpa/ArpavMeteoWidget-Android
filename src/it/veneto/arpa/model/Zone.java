package it.veneto.arpa.model;

import java.io.Serializable;
import java.util.HashMap;
import it.veneto.arpa.model.Province;
import java.util.List;
import java.util.ArrayList;

/**
 * Class that represent the information of all zone in Veneto
 * @author Luca
 *
 */
public class Zone implements Serializable {
    private String id; //
    private String name; //
    private static final long serialVersionUID = 7526471155622776147L; //
    private HashMap<String, Province> provinces; //

    public Zone() {
        provinces = new HashMap<String, Province>();
    }

    public Zone(String id) {
        provinces = new HashMap<String, Province>();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Zone setName(String name) {
        this.name = name;
        return this;
    }

    public void addProvince(String name, Province province) {
        provinces.put(name, province);
    }

    public String getProvinceName(String name) {
        if (provinces.containsKey(name)) {
            return provinces.get(name).getName();
        }
        else {
            return "";
        }
    }

    /**
     * Get all provinces name in Veneto
     * @return list of provinces name
     */
    public List<String> getProvincesName() {
        List<String> provincesName = new ArrayList<String>	();

        for (Province i : provinces.values()) {
            provincesName.add(i.getName());
        }

        return provincesName;
    }

    /**
     * Get a particular province
     * @param provinceName
     * @return province object
     */
    public Province getProvince(String provinceName) {
        return provinces.get(provinceName);
    }
}