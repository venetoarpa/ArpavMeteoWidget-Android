package it.veneto.arpa.util.parser;
import it.veneto.arpa.model.Zone;
import it.veneto.arpa.util.handler.ZoneHandler;


import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import android.os.AsyncTask;
import android.content.Context;

/**
 * Sax xml parser for zone
 * @author Luca
 *
 */
public class ParserZone extends AsyncTask<String, Void, Zone> {
    Context context;

    public ParserZone(Context context) {
        this.context = context;
    }

    /**
     * Initialize Sax Parser Reader
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private XMLReader initializeReader() throws ParserConfigurationException, SAXException {
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // create a parser
        SAXParser parser = factory.newSAXParser();
        // create the reader (scanner)
        XMLReader xmlreader = parser.getXMLReader();
        return xmlreader;
    }

    protected Zone doInBackground(String... urls) {
        publishProgress();
        return parseZone(urls[0]);
    }

    /**
     * Parsing xml
     * @param xml
     * @return
     */
    public Zone parseZone(String xml) {
        try {
            XMLReader xmlreader = initializeReader();
            ZoneHandler zoneHandler = new ZoneHandler();
            xmlreader.setContentHandler(zoneHandler);
            URL xmlURL = new URL(xml);
            URLConnection xmlConn = xmlURL.openConnection();
            InputStreamReader xmlStream = new InputStreamReader(xmlConn.getInputStream());
            BufferedReader xmlBuff = new BufferedReader(xmlStream);
            InputSource is = new InputSource(xmlBuff);
            is.setEncoding("UTF-8");
            xmlreader.parse(is);
            return zoneHandler.retrieveZone();
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
