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

public class WeatherApp {

    private String city;
    private String country;

    private static String KEY = "f706430bae10de65bf9eacb0eeb77df9";

    private WeatherApp(String city, String country) {
        this.city = city;
        this.country = country;

    }

    public void getWeather() throws Exception{

        String api = "https://api.openweathermap.org/data/2.5/forecast?q=" + this.city  +"," + this.country + "&mode=xml&appid=" + KEY;
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
        System.out.println(nodeList.item(0).getAttributes().getNamedItem("rise").getNodeValue());

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (rootNode.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;
                System.out.println(element.getElementsByTagName("name").item(0).getTextContent());
            }
        }


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
