package it.veneto.arpa.util.handler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import it.veneto.arpa.model.Bulletin;
import it.veneto.arpa.model.Meteogram;
import it.veneto.arpa.model.Day;
import it.veneto.arpa.model.Temperature;

/**
 * Sax Handler to parse meteogram
 * @author Luca
 *
 */
public class MeteogramsHandler extends DefaultHandler {
    private StringBuffer buffer = new StringBuffer();
    private Meteogram meteogram;
    private Bulletin bulletin;
    private Day day;
    private Temperature temp;

    /**
     * @see Sax startElement
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        buffer.setLength(0);

        if(localName.equalsIgnoreCase("bulletin")) {
            bulletin = new Bulletin(atts.getValue("date"), atts.getValue("time"));
        }
        else if (localName.equalsIgnoreCase("meteogram")) {
            meteogram = new Meteogram(atts.getValue("zoneid"));
        }
        else if (localName.equalsIgnoreCase("day")) {
            day = new Day();
            day.setDate(atts.getValue("date"));
            day.setTime(atts.getValue("time"));
        }
        else if (localName.equalsIgnoreCase("temp")) {
            temp = new Temperature();
            temp.setMt(atts.getValue("meters"));
            temp.setMax(atts.getValue("max"));
            temp.setMin(atts.getValue("min"));
            day.addTemp(temp);
        }
        else if (localName.equalsIgnoreCase("sky")) {
            day.setSkyImg(atts.getValue("img"));
        }
        else if (localName.equalsIgnoreCase("rain")) {
            day.setrainPerc(atts.getValue("perc"));
        }
    }

    /**
     * @see Sax endElement
     */
    public void endElement(String uri, String localName, String qName)throws SAXException {
        if (localName.equalsIgnoreCase("meteogram")) {
            bulletin.insertMeteogram(meteogram.getZoneId(), meteogram);
        }
        else if (localName.equalsIgnoreCase("day")) {
            meteogram.insertDay(day);
        }
        else if (localName.equalsIgnoreCase("sky")) {
            day.setSkyDescription(buffer.toString());
        }
        else if (localName.equalsIgnoreCase("rain")) {
            day.setRainDescription(buffer.toString());
        }
    }

    public void characters(char[] ch, int start, int length) {
        buffer.append(ch, start, length);
    }

    public Bulletin retrieveBulletin() {
        return bulletin;
    }
}
