package it.veneto.arpa.util.handler;

import it.veneto.arpa.model.Zone;
import it.veneto.arpa.model.Province;
import it.veneto.arpa.model.City;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Sax Handler to parse zone
 * @author Luca
 *
 */
public class ZoneHandler extends DefaultHandler {
    private StringBuffer buffer = new StringBuffer();
    private Zone zone;
    private boolean boolProvincia = false;
    private boolean boolProvinciaName = false;
    private boolean boolCity = false;
    private boolean boolCityId = false;
    private Province province;
    private City city;

    /**
     *  @see Sax startElement
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        buffer.setLength(0);

        if(localName.equalsIgnoreCase("plist")) {
            zone = new Zone();
        }
    }

    /**
     *  @see Sax endElement
     */
    public void endElement(String uri, String localName, String qName)throws SAXException {
        if(localName.equalsIgnoreCase("array") && boolProvincia) {
            zone.addProvince(province.getName(), province);
            boolProvincia = false;
            boolProvinciaName = false;
        }
        else if(localName.equalsIgnoreCase("string")) {
            if(boolProvincia && !boolProvinciaName) {
                province.setName(buffer.toString());
                boolProvinciaName = true;
            }
            else if(boolProvincia && boolCity) {
                city.setName(buffer.toString());
            }
        }
        else if(localName.equalsIgnoreCase("key")) {
            if(buffer.toString().equals("provincia")) {
                boolProvincia = true;
                province = new Province();
            }
            else if(buffer.toString().equals("id")) {
                city = new City();
                boolCity = true;
            }
        }
        else if(localName.equalsIgnoreCase("integer")) {
            if (boolCity && !boolCityId) {
                city.setCityId(buffer.toString());
                boolCityId = true;
            }
            else if (boolCity && boolCityId) {
                city.setZoneId(buffer.toString());
                boolCityId = false;
                province.addCity(city);
                boolCity = false;
            }
        }
    }

    public void characters(char[] ch, int start, int length) {
        buffer.append(ch, start, length);
    }

    public Zone retrieveZone() {
        return zone;
    }
}
