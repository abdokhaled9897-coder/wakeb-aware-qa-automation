package com.wakeb.aware.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    public static String get(String key) throws IOException {

        Properties properties = new Properties();

        try (InputStream file = ConfigReader.class
                .getResourceAsStream("/config.properties")) {

            if (file == null) {
                throw new IOException("config.properties was not found");
            }

            properties.load(file);
        }

        return properties.getProperty(key);
    }
}