package com.example.demo.service;

import com.example.demo.converter.LanguageConverter;
import com.example.demo.enums.Language;
import com.example.demo.properties.jdoodle.JdoodleProperties;
import com.example.demo.request.ExecuteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JdoodleServiceImpl implements JdoodleService {

    private RestTemplate restTemplate;
    private JdoodleProperties jdoodleProperties;
    private LanguageConverter languageConverter;

    @Autowired
    public JdoodleServiceImpl(
        RestTemplate restTemplate,
        JdoodleProperties jdoodleProperties,
        LanguageConverter languageConverter
    ) {
        this.restTemplate = restTemplate;
        this.jdoodleProperties = jdoodleProperties;
        this.languageConverter = languageConverter;
    }

    public String executeCode(String code, String input, Language language) {
        if (!jdoodleProperties.isEnabled()) {
           return "";
        }
        var languageVersion = languageConverter.toPropertiesLanguage(jdoodleProperties.getLanguageVersions(), language);
        ExecuteRequest request = ExecuteRequest.builder()
            .stdin(input)
            .versionIndex(languageVersion.version())
            .language(languageVersion.name())
            .compileOnly(false)
            .script(code)
            .clientId(jdoodleProperties.getClientId())
            .clientSecret(jdoodleProperties.getClientSecret())
            .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<ExecuteRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            jdoodleProperties.getUrl(),
            HttpMethod.POST,
            entity,
            String.class
        );

        return extractOutput(response.getBody());
    }

    private String extractOutput(String body) {
        String regex = "\\w?\"output\":\"(\\w+)\"";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(body);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            System.out.println("No match found." + body);
        }
        return null;
    }
}
