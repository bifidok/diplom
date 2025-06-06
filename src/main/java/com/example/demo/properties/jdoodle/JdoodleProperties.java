package com.example.demo.properties.jdoodle;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jdoodle")
@Getter
@Setter
public class JdoodleProperties {
    private boolean enabled;
    private String url;
    private String clientId;
    private String clientSecret;
    private LanguageVersions languageVersions;
}
