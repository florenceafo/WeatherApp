package weatherapp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import java.io.*;
import java.net.URL;
import static java.lang.Integer.parseInt;

public class WeatherApp {

    private String city;
    private String country;
    private int actualHoursDayLight;
    private int actualMinutesDaylight;
    private int sunRiseHour;
    private int sunRiseMinute;
    private int sunSetHour;
    private int sunSetMinute;

    // Harvey Balls unicode symbols to visualise time
    private static final String HOUR_SYMBOL = "\u25CF ";
    private static final String THREE_QUARTER_HOUR_SYMBOL = "\u25D5 ";
    private static final String HALF_HOUR_SYMBOL = "\u25D1 ";
    private static final String QUARTER_HOUR_SYMBOL = "\u25D4 ";

    // Key needed to get data from the OpenWeatherMaps API
    private static final String API_KEY = "f706430bae10de65bf9eacb0eeb77df9";

    // Categories, for rounding remaining minutes
    private static final int MINUTE_QUART = 15;
    private static final int MINUTE_HALF = 30;
    private static final int MINUTE_THREE_QUART = 45;
    private static final int MINUTE_FULL = 60;


    public WeatherApp(String city, String country) {

        this.city = city;
        this.country = country;
        actualHoursDayLight = 0;
        actualMinutesDaylight = 0;
        sunRiseHour = 0;
        sunRiseMinute = 0;
        sunSetHour = 0;
        sunSetMinute = 0;
    }

    public String getCity() {
        return city;
    }

    // Produces the visualisation of the daylight hours
    public String toString(int roundMinute) {

        String visualDaylight = "";

        // Full hours
        for (int i = 0; i < actualHoursDayLight; i++) {
            visualDaylight += HOUR_SYMBOL;
        }

        // Remainder
        switch (roundMinute) {
            case MINUTE_QUART:
                visualDaylight += QUARTER_HOUR_SYMBOL;
                break;
            case MINUTE_HALF:
                visualDaylight += HALF_HOUR_SYMBOL;
                        break;
            case MINUTE_THREE_QUART:
                visualDaylight += THREE_QUARTER_HOUR_SYMBOL;
                break;
            case MINUTE_FULL:
                visualDaylight += HOUR_SYMBOL;
                break;
        }

        return visualDaylight;
    }

    public Document saveWeatherData() throws Exception {

        // Creates URL using parameters city, country and api key
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + this.city + "," + this.country + "&mode=xml&appid=" + API_KEY;
        URL url = new URL(apiUrl);

        // Builds the document, read contents of the url with openStream()
        Document doc = buildFactory().parse(url.openStream());

        // Write the data to an xml file plus transformation
        DOMSource sourceInput = new DOMSource(doc);
        FileWriter writer = new FileWriter(new File("weather.xml"));
        // Holds the transformed data
        StreamResult resultData = new StreamResult(writer);
        // Transforms the DOM object to the XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        //Transform source to result
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(sourceInput, resultData);

        // Build document
        Document document = buildFactory().parse(new File("weather.xml"));

        return document;
    }

    public int parseWeatherFile(Document document) {
        // Normalise the XML structure
        document.getDocumentElement().normalize();

        // Get the root node
        Element rootNode = document.getDocumentElement();

        // Get the current weather
        NodeList nodeList = document.getElementsByTagName("sun");

        // Parse file to get data related to sun rise and sun set
        String sunRiseData = nodeList.item(0).getAttributes().getNamedItem("rise").getNodeValue();
        String sunSetData = nodeList.item(0).getAttributes().getNamedItem("set").getNodeValue();

        // Extract the hour and minute of sun rise
        int sunRiseDataT = sunRiseData.indexOf('T');
        sunRiseHour = parseInt(sunRiseData.substring(sunRiseDataT + 1, sunRiseDataT + 3));
        sunRiseMinute = parseInt(sunRiseData.substring(sunRiseDataT + 4, sunRiseDataT + 6));

        // Extract the hour and minute of sun set
        int sunSetDataT = sunSetData.indexOf('T');
        sunSetHour = parseInt(sunSetData.substring(sunSetDataT + 1, sunSetDataT + 3));
        sunSetMinute = parseInt(sunSetData.substring(sunSetDataT + 4, sunSetDataT + 6));

        // Calculates the actual hours and minutes of daylight
        actualHoursDayLight = sunSetHour - (sunRiseHour + 1);
        actualMinutesDaylight = sunSetMinute + (60 - sunRiseMinute);
        if (actualMinutesDaylight >= 60) {
            actualHoursDayLight++;
            actualMinutesDaylight -= 60;
        }

        System.out.println();
        System.out.println("Today in " + this.getCity() + ", there will be " + actualHoursDayLight + ":" + actualMinutesDaylight + " hours of daylight");
        System.out.println();
        // Round minutes to nearest quarter hour
        int roundMinute = (int) ((Math.round(actualMinutesDaylight/60.0*4)/4f)*60);
        return roundMinute;

    }

    // Creates the document builder factory
    public DocumentBuilder buildFactory() throws Exception{
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder;
    }

    public void getDaylightHours () throws Exception {
        // Access the api data and save it to a new xml file
        Document document = saveWeatherData();
        // Parses the weather data, extracts the total daylight time
        // Returns the remainder after the last full hour, rounded to the nearest 15 minutes
        int roundMinute = parseWeatherFile(document);
        // Uses the number of whole hours plus the rounded quarter to print a visualisation to the console
        System.out.println(toString(roundMinute));
    }

}
