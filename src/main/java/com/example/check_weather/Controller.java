package com.example.check_weather;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/process")
public class Controller {

    @Autowired
    private ZeebeClient zeebeClient;

    @GetMapping("/start")
    private void startProcessInstance() {
        String processID = "current-weather";
        System.out.println("Starting process `" + processID);

        zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId(processID)
                .latestVersion()
                .send();

    }
}
