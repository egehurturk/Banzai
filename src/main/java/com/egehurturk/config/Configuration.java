package com.egehurturk.config;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

public class Configuration {
    private Properties config;
    private static String CONFIG_PROP_FILE = "server.properties";
    protected static String PORT_PROP = "server.port";
    protected static String HOST_PROP = "server.host";
    protected static String NAME_PROP = "server.name";
    protected static String WEBROOT_PROP = "server.webroot";
    protected InputStream propertiesStream = ClassLoader.getSystemClassLoader()
            .getResourceAsStream( CONFIG_PROP_FILE );
    protected String name;
    protected String webRoot;
    protected InetAddress serverHost;
    protected int serverPort;

    public Configuration(Properties configurator) {
        this.config = configurator;
        prepareFields(this.config);
    }

    private void prepareFields(Properties config) {
        setNameProp(config.getProperty(NAME_PROP));
        setHostProp(config.getProperty(HOST_PROP));
        setPortProp(config.getProperty(PORT_PROP));
        setWebrootProp(config.getProperty(WEBROOT_PROP));
    }


    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }

    public static String getConfigPropFile() {
        return CONFIG_PROP_FILE;
    }

    public static void setConfigPropFile(String configPropFile) {
        CONFIG_PROP_FILE = configPropFile;
    }

    public static String getPortProp() {
        return PORT_PROP;
    }

    public static void setPortProp(String portProp) {
        PORT_PROP = portProp;
    }

    public static String getHostProp() {
        return HOST_PROP;
    }

    public static void setHostProp(String hostProp) {
        HOST_PROP = hostProp;
    }

    public static String getNameProp() {
        return NAME_PROP;
    }

    public static void setNameProp(String nameProp) {
        NAME_PROP = nameProp;
    }

    public static String getWebrootProp() {
        return WEBROOT_PROP;
    }

    public static void setWebrootProp(String webrootProp) {
        WEBROOT_PROP = webrootProp;
    }

    public InputStream getPropertiesStream() {
        return propertiesStream;
    }

    public void setPropertiesStream(InputStream propertiesStream) {
        this.propertiesStream = propertiesStream;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebRoot() {
        return webRoot;
    }

    public void setWebRoot(String webRoot) {
        this.webRoot = webRoot;
    }

    public InetAddress getServerHost() {
        return serverHost;
    }

    public void setServerHost(InetAddress serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
