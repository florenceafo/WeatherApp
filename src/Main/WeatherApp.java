package Main;

public class WeatherApp {

    private String city;
    private String country;
    private static String key = "f706430bae10de65bf9eacb0eeb77df9";

    private WeatherApp(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public void getWeather() {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + this.city  +"," + this.country + "&mode=xml&appid=" + key;
        System.out.println(url);
    }

    public static void main(String[] args) {

        String city = "Gothenburg";
        String country = "se";
        WeatherApp weather = new WeatherApp(city, country);
        weather.getWeather();

    }
}
