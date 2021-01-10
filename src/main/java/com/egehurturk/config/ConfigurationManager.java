package com.egehurturk.config;

import com.egehurturk.exceptions.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    private static ConfigurationManager manager;
    private static Configuration currentConfig;
//    private static String CONFIG_PROP_FILE ="server.properties";
//
//    protected InputStream propertiesStream = ClassLoader.getSystemClassLoader()
//            .getResourceAsStream( CONFIG_PROP_FILE );

    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if (manager == null)
            manager = new ConfigurationManager();
        return manager;
    }

    public void loadConfigurationFile(String filePath) throws ConfigurationException {
        currentConfig = new Configuration(serveConfigurations(System.getProperties(), filePath));
    }

    public Properties serveConfigurations(Properties userConfig, String file) {
        InputStream propertiesStream = ClassLoader.getSystemClassLoader().getResourceAsStream(file);
        Properties serverConfig = new Properties();
        try {
            if (propertiesStream == null) {
                throw new IOException("System Configuration Error: Are you sure that a properties" +
                        " file is located under resources folder as stated in standard Maven " +
                        " directory template?");
            }

            serverConfig.load(propertiesStream);

            userConfig.keySet().
                    forEach(val -> {
                        String key = (String) val;
                        String value = userConfig.getProperty(key);
                        if (value != null)
                            serverConfig.put(key, value);
                    });
        }
        catch (IOException err) {
            System.err.println("System Configuration Error: Are you sure that a properties" +
                    " file is located under resources folder as stated in standard Maven " +
                    " directory template?");
        }
        return serverConfig;
    }

    public Configuration getCurrentConfiguration() throws ConfigurationException {
        if ( manager == null) {
            throw new ConfigurationException("No Current Configuration Set.");
        }
        return currentConfig;
    }
}
