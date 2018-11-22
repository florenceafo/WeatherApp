package weatherapp;

public class Main {

    public static void main(String[] args) {

        try {
            String city = "Gothenburg";
            String country = "se";
            WeatherApp weather = new WeatherApp(city, country);
            weather.getDaylightHours();

        } catch (Exception e) {
            e.getMessage();
        }

    }
}
