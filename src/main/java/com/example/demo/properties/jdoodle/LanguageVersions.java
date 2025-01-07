package com.example.demo.properties.jdoodle;

public class LanguageVersions {
    private Language java;
    private Language c;
    private Language cSharp;
    private Language cPlus;
    private Language pascal;
    private Language python;
}

record Language(String name, String version) {

}
