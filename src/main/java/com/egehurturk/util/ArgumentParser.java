package com.egehurturk.util;

import com.egehurturk.exceptions.ConfigurationException;
import com.egehurturk.handlers.HttpHandler;
import com.egehurturk.httpd.HttpServer;
import org.apache.commons.cli.*;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Command Line Argument Parser
 */
public class ArgumentParser {
    public Options options = new Options();
    public CommandLineParser parser = new DefaultParser();
    public CommandLine cmd = null;
    public String[] args;
    private HttpServer httpServer;
    private HttpHandler handler;


    public ArgumentParser(String[] args) {
        generateOptions();
        this.args = args;
        try {
            httpServer = new HttpServer();
            cmd = parser.parse(this.options, args);
            parse();
        } catch (ParseException | UnknownHostException | FileNotFoundException err) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("banzai", options);
        }

    }


    private void parse() throws UnknownHostException, FileNotFoundException {
        if (cmd.hasOption("config")) {
            httpServer.setConfigPropFile(cmd.getOptionValue("config"));
            try {
                httpServer.configureServer();
            } catch (ConfigurationException e) {
                System.err.println("Configuration exception: Check configuraton property file path");
                return;
            }
            handler = new HttpHandler(httpServer.getConfig());
        } else if (cmd.hasOption("port") && cmd.hasOption("host") && cmd.hasOption("name") && cmd.hasOption("webroot") && cmd.hasOption("backlog")) {
            httpServer = new HttpServer(Integer.parseInt(cmd.getOptionValue("port")), InetAddress.getByName(cmd.getOptionValue("host")), Integer.parseInt(cmd.getOptionValue("backlog")), cmd.getOptionValue("name"), cmd.getOptionValue("webroot"));
            handler = new HttpHandler(httpServer.getWebRoot(), httpServer.getName());
        } else if (cmd.hasOption("port") && cmd.getArgs().length == 1) {
            httpServer = new HttpServer(Integer.parseInt(cmd.getOptionValue("port")));
            handler = new HttpHandler(httpServer.getWebRoot(), httpServer.getName());
        } else {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("banzai", options);
        }
    }

    public HttpServer getHttpServer() {
        return this.httpServer;
    }

    public HttpHandler getHandler() {
        return handler;
    }

    private void generateOptions() {
        Options options = new Options();
        Option port = Option.builder()
                .longOpt("port")
                .argName("portNumber")
                .hasArg()
                .desc("bind server to a port. Default is 9090" )
                .build();
        Option name = Option.builder()
                .longOpt("name")
                .argName("name" )
                .hasArg()
                .desc("Use name for web server" )
                .build();
        Option host = Option.builder()
                .longOpt("host")
                .argName("host" )
                .hasArg()
                .desc("Bind server to host" )
                .build();
        Option webroot = Option.builder()
                .longOpt("webroot")
                .argName("webroot" )
                .hasArg()
                .desc("use given directory to store HTML/source files" )
                .build();
        Option backlog = Option.builder()
                .longOpt("backlog")
                .argName("backlog" )
                .hasArg()
                .desc("backlog for server" )
                .build();
        Option config = Option.builder()
                .longOpt("config")
                .argName("config" )
                .hasArg()
                .desc("Configuration system properties file for server" )
                .build();
        this.options.addOption(port);
        this.options.addOption(host);
        this.options.addOption(name);
        this.options.addOption(webroot);
        this.options.addOption(backlog);
        this.options.addOption(config);
    }

}
