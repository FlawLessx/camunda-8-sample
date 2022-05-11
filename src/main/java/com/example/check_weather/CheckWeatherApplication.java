package com.example.check_weather;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(resources = "classpath*:/bpmn/*.bpmn")
public class CheckWeatherApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckWeatherApplication.class, args);
	}

	@ZeebeWorker(type = "CheckWeather")
	public void CheckWeatherWorker(final JobClient client, final ActivatedJob job) {

		Map<String, Object> variablesAsMap = job.getVariablesAsMap();
		String city = (String) variablesAsMap.get("city");

		CheckWeather weather = new CheckWeather();
		try {
			System.out.println("Going to check weather for city "+ city);
			WeatherDetails details = weather.getDetails(city);

			if(details != null) {
				city = details.getLocation().getCity();
				String country = details.getLocation().getCountry();
				String localTime = details.getLocation().getLocaltime();
				Double tempC = details.getCurrent().getTempC();
				Double windMph = details.getCurrent().getWindMph();

				HashMap<String, Object> variables = new HashMap<>();
				variables.put("city", city);
				variables.put("country", country);
				variables.put("local_time", localTime);
				variables.put("temp_c", tempC);
				variables.put("wind", windMph);

				System.out.println("Complete process for city " + city);

				client.newCompleteCommand(job.getKey())
						.variables(variables)
						.send()
						.exceptionally((throwable -> {
							throw new RuntimeException("Could not complete job", throwable);
						}));
			}else {
				client.newThrowErrorCommand(job.getKey())
						.errorCode("city_not_found")
						.errorMessage("Wasn't able to find city")
						.send()
						.exceptionally((throwable -> {
							throw new RuntimeException("Could not throw the BPMN Error Event", throwable);
						}));
			}
		}catch(Exception e){
			e.printStackTrace();
			client.newFailCommand(job.getKey());
		}
	}
}
