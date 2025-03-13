package com.example.demo.service;

import com.example.demo.converter.LanguageConverter;
import com.example.demo.dto.CodeExecutionMessage;
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

    public CodeExecutionMessage executeCode(String code, String input, Language language) {
        if (!jdoodleProperties.isEnabled()) {
           return CodeExecutionMessage.builder().build();
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
        if (hasErrors(response.getBody())) {
            return CodeExecutionMessage.builder()
                .error(extractError(response.getBody()))
                .build();
        }
        return CodeExecutionMessage.builder()
            .output(extractOutput(response.getBody()))
            .build();
    }

    private String extractOutput(String body) {
        String regexOutput = "\\w?\"output\":\"(\\w+)\"";

        Pattern pattern = Pattern.compile(regexOutput);
        Matcher matcher = pattern.matcher(body);

        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalStateException(String.format("No output in body %s", body));
    }

    private String extractError(String body) {
        String regexCodeErrorMessage = "\"output\"\\s*:\\s*\"((\\\\.|[^\"\\\\])*)\"";
        String regexCodeNullMessage = "\"output\"\\s*:null";
        Pattern pattern = Pattern.compile(regexCodeErrorMessage);
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }
        Pattern patternNull = Pattern.compile(regexCodeNullMessage);
        Matcher matcherNull = patternNull.matcher(body);
        if (matcherNull.find()) {
            var output = matcherNull.group(1);
            if (output.isEmpty() || "null".equals(output)) {
                return "Something went wrong";
            }
        }
        throw new IllegalStateException(String.format("No output in body %s", body));
    }

    private boolean hasErrors(String body){
        String regexCodeError = "\\w?\"isExecutionSuccess\":false";
        String regexCodeErrorNull = "\"isExecutionSuccess\":null";
        Pattern pattern = Pattern.compile(regexCodeError);
        Pattern patternNull = Pattern.compile(regexCodeErrorNull);
        return pattern.matcher(body).find() || patternNull.matcher(body).find();
    }
}
