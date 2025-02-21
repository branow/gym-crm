package dev.branow.utils;

import dev.branow.exceptions.WebServerException;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WebServerFactory {

    public Tomcat getTomcatServer(WebApplicationContext context) {
        var tomcat = new Tomcat();
        tomcat.setBaseDir(createTempTomcatDir());
        tomcat.getHost().setAppBase(".");
        tomcat.getHost().setAutoDeploy(false);
        setupDispatcherServlet(tomcat, context);
        return tomcat;
    }

    private void setupDispatcherServlet(Tomcat tomcat, WebApplicationContext context) {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        Context tomcatContext = tomcat.addContext(tomcat.getHost(), "", null);
        Tomcat.addServlet(tomcatContext, "dispatcher", dispatcherServlet).setLoadOnStartup(1);
        tomcatContext.addServletMappingDecoded("/", "dispatcher");
    }

    private String createTempTomcatDir() {
        try {
            File tempDir = Files.createTempDirectory("tomcat").toFile();
            tempDir.deleteOnExit();
            return tempDir.getAbsolutePath();
        } catch (IOException ex) {
            throw new WebServerException("Unable to create temp tomcat dir", ex);
        }
    }

}
