package com.example.check_weather;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherDetails {
    public Location location;
    public Current current;

    @JsonCreator
    public WeatherDetails(
            @JsonProperty("location") Location location,
            @JsonProperty("current") Current current) {

        super();
        this.location = location;
        this.current = current;
    }
}

@Getter
@Setter
class Location {
    @JsonProperty("name")
    public String city;

    @JsonProperty("country")
    public String country;

    @JsonProperty("localtime")
    public String localtime;
}

@Getter
@Setter
class Current {
    @JsonProperty("temp_c")
    public Double tempC;

    @JsonProperty("wind_mph")
    public Double windMph;
}
