package com.example.demo.service;

import com.example.demo.properties.jdoodle.JdoodleProperties;
import com.example.demo.request.ExecuteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JdoodleServiceImpl implements JdoodleService {

    private RestTemplate restTemplate;
    private JdoodleProperties jdoodleProperties;

    @Autowired
    public JdoodleServiceImpl(
        RestTemplate restTemplate,
        JdoodleProperties jdoodleProperties
    ) {
        this.restTemplate = restTemplate;
        this.jdoodleProperties = jdoodleProperties;
    }

    public String executeCode(String code) {
        if (!jdoodleProperties.isEnabled()) {
           return "";
        }
        ExecuteRequest request = ExecuteRequest.builder()
            .stdin("2 4")
            .versionIndex("5")
            .language("java")
            .compileOnly(false)
            .script(code)
            .clientId(jdoodleProperties.getClientId())
            .clientSecret(jdoodleProperties.getClientSecret())
            .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<ExecuteRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "https://api.jdoodle.com/v1/execute",
            HttpMethod.POST,
            entity,
            String.class
        );

        var body = response.getBody();
        System.out.println(body);
        return body;
    }
}
