package Main;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import java.io.*;
import java.net.URL;

import static java.lang.Integer.min;
import static java.lang.Integer.parseInt;

public class WeatherApp {

    private String city;
    private String country;

    private static String KEY = "f706430bae10de65bf9eacb0eeb77df9";

    private WeatherApp(String city, String country) {
        this.city = city;
        this.country = country;

    }

    public void getWeather() throws Exception {

        String api = "https://api.openweathermap.org/data/2.5/forecast?q=" + this.city + "," + this.country + "&mode=xml&appid=" + KEY;
        URL url = new URL(api);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(url.openStream());

        // Write the data to an xml file
        DOMSource source = new DOMSource(doc);
        FileWriter writer = new FileWriter(new File("weather.xml"));
        StreamResult result = new StreamResult(writer);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);

        // Get document builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Build document
        Document document = builder.parse(new File("weather.xml"));

        // Normalise the XML structure
        document.getDocumentElement().normalize();

        // Get the root node
        Element rootNode = document.getDocumentElement();
        System.out.println(rootNode.getNodeName());

        // Get the current weather
        NodeList nodeList = document.getElementsByTagName("sun");

        int sunRiseHour = 0;
        int sunRiseMinute = 0;
        int sunSetHour = 0;
        int sunSetMinute = 0;
        String sunRiseData = nodeList.item(0).getAttributes().getNamedItem("rise").getNodeValue();
        String sunSetData = nodeList.item(0).getAttributes().getNamedItem("set").getNodeValue();
        int sunRiseDataT = sunRiseData.indexOf('T');

        sunRiseHour = parseInt(sunRiseData.substring(sunRiseDataT + 1, sunRiseDataT + 3));
        sunRiseMinute = parseInt(sunRiseData.substring(sunRiseDataT + 4, sunRiseDataT + 6));

        int sunSetDataT = sunSetData.indexOf('T');

        sunSetHour = parseInt(sunSetData.substring(sunSetDataT + 1, sunSetDataT + 3));
        sunSetMinute = parseInt(sunSetData.substring(sunSetDataT + 4, sunSetDataT + 6));

        System.out.println(sunRiseHour);
        System.out.println(sunRiseMinute);
        System.out.println(sunSetHour);
        System.out.println(sunSetMinute);

        double hoursDayLight = 0.0;

        int hours = sunSetHour - (sunRiseHour + 1);
        int minutes = sunSetMinute + (60 - sunRiseMinute);
        if (minutes >= 60) {
            hours++;
            minutes -= 60;
        }

        double minuteRound = 0;
        if (minutes < 15) {
            minuteRound = 0.25;
        } else if (minutes < 30) {
            minuteRound = 0.5;
        } else if (minutes < 45) {
            minuteRound = 0.75;
        } else {
            minuteRound = 1;
        }

        hoursDayLight = hours + minuteRound;
        System.out.println(hoursDayLight);

        System.out.println("daylight: " + hours + ":" + minutes);

        

    }

    public static void main(String[] args) {

        try {
            String city = "Gothenburg";
            String country = "se";
            WeatherApp weather = new WeatherApp(city, country);
            weather.getWeather();
        } catch (Exception e) {

        }


    }
}
