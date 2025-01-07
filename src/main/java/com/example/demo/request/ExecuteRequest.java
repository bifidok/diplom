package com.example.demo.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExecuteRequest {
    private String clientId;
    private String clientSecret;
    private String script;
    private String stdin;
    private String language;
    private String versionIndex;
    private boolean compileOnly;
}

