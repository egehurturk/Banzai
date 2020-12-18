package com.egehurturk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public class HttpServer extends BaseServer {

    public HttpServer(int serverPort) throws UnknownHostException {
        super(serverPort);
    }

    public HttpServer() {
        super();
    }

    public HttpServer(int serverPort, InetAddress serverHost, int backlog) {
        super(serverPort, serverHost, backlog);
    }

    @Override
    public int getServerPort() {
        return super.getServerPort();
    }

    @Override
    public InetAddress getServerHost() {
        return super.getServerHost();
    }

    @Override
    public int getBacklog() {
        return super.getBacklog();
    }

    @Override
    public BufferedReader getIn() {
        return super.getIn();
    }

    @Override
    public PrintWriter getOut() {
        return super.getOut();
    }

    @Override
    public String getConfigPropFile() {
        return super.getConfigPropFile();
    }

    @Override
    public void setConfigPropFile(String configPropFile) {
        super.setConfigPropFile(configPropFile);
    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public void stop() throws IOException {

    }

    @Override
    public void restart() {
        super.restart();
    }

    @Override
    public void reload() {
        super.reload();
    }

    @Override
    public void configureServer() {
        super.configureServer();
    }

    @Override
    public void configureServer(String propertiesFilePath) {
        super.configureServer(propertiesFilePath);
    }

    @Override
    protected Properties serveConfigurations(Properties userConfig, InputStream file) {
        return super.serveConfigurations(userConfig, file);
    }

    @Override
    protected ConnectionManager createManager(Socket cli) {
        return super.createManager(cli);
    }
}
