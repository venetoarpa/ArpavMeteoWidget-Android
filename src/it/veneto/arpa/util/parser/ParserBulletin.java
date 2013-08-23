package it.veneto.arpa.util.parser;

import android.os.AsyncTask;
import it.veneto.arpa.model.Bulletin;
import it.veneto.arpa.util.handler.MeteogramsHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Sax xml parser for bulletin
 * @author Luca
 *
 */
public class ParserBulletin extends AsyncTask<String, Void, Bulletin> {

    public ParserBulletin() {
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

    protected Bulletin doInBackground(String... urls) {
        return parseMeteograms(urls[0]);
    }

    /**
     * Parsing xml
     * @param xml
     * @return
     */
    public Bulletin parseMeteograms(String xml) {
        try {
            XMLReader xmlreader = initializeReader();
            MeteogramsHandler meteogramsHandler = new MeteogramsHandler();
            xmlreader.setContentHandler(meteogramsHandler);
            URL xmlURL = new URL(xml);
            URLConnection xmlConn = xmlURL.openConnection();
            InputStreamReader xmlStream = new InputStreamReader(xmlConn.getInputStream());
            BufferedReader xmlBuff = new BufferedReader(xmlStream);
            InputSource is = new InputSource(xmlBuff);
            is.setEncoding("UTF-8");
            xmlreader.parse(is);
            return meteogramsHandler.retrieveBulletin();
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
