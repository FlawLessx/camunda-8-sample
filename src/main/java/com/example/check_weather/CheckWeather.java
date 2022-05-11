package com.example.check_weather;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CheckWeather {

    public static String apiKey;

    @Value("${weather.api-key}")
    public void setApiKey(String key){
        apiKey = key;
    }

    public WeatherDetails getDetails(String cityName) throws Exception {
        System.out.println("Making call for city: "+ cityName);
        HttpResponse<String> response = get("http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q="+ cityName+ "&aqi=no");

        if (response.statusCode() != 200) {

            // create incidence if Status code is not 200#
            System.out.println("Error Status Code: "+ response.statusCode());
            throw new Exception("Error from REST call, Response code: " + response.statusCode());

        } else {
            if (response.body() == null){
                return null;
            }

            ObjectMapper om = new ObjectMapper();
            om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            WeatherDetails weatherDetails = om.readValue(response.body(), WeatherDetails.class);
            System.out.println(weatherDetails.toString());
            return weatherDetails;
        }
    }

    private HttpResponse<String> get(String uri) throws Exception {

        uri = uri.replace(" ", "+");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        return response;
    }
}
