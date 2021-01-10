package com.egehurturk.exceptions;

public class ConfigurationException extends Exception {
    public ConfigurationException(String errMsg) {super(errMsg);}
    public ConfigurationException(String errMsg, Throwable cause) {
        super(errMsg, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationException() {
    }
}
