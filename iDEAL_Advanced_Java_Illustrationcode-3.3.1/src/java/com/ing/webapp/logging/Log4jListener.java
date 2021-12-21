// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 7/10/2012 11:46:52 AM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Log4jListener.java

package com.ing.webapp.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class Log4jListener implements ServletContextListener {

    private static final Logger log;
    private final Timer timer;
    private long lastConfigure;
    private File location;

    static {
        log = LogManager.getLogger(com.ing.webapp.logging.Log4jListener.class);
    }

    public static void main(String args[]) {

        try {
            FileOutputStream s = new FileOutputStream(new File("___.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Log4jListener() {
        timer = new Timer();
        lastConfigure = 1L;
        System.out.println("Log4jListener: init");
    }

    public void contextInitialized(ServletContextEvent event) {
        System.out.println("Log4jListener: Searching for init parameter \"log4j.xml\" ...");
        String logConfigLocation = event.getServletContext().getInitParameter("log4j.xml");
        if (logConfigLocation != null) {
            location = new File(logConfigLocation);
            System.out.println("Log4jListener: Using external log configuration in " + location.getAbsolutePath());
        } else {
            System.out.println("Log4jListener: Unable to find external log configuration defined by init-parameter \"\".");
            System.out.println("Log4jListener: Will use default internal log configuration \"/WEB-INF/log4j2.xml\".");
        }

        String defaultLocation = event.getServletContext().getRealPath("/WEB-INF/log4j2.xml");

        if (location != null || defaultLocation != null) {
            configure(getUri(defaultLocation));
            if (location != null && location.exists()) {
                if (log.isInfoEnabled()) {
                    System.out.println("Log4jListener: Starting background watchdog thread...");
                    log.info("Log4jListener: Starting background watchdog thread...");
                }
                long timerDelay = 5000L;
                if (event.getServletContext().getInitParameter("timerDelay") != null)
                    try {
                        timerDelay = Long.parseLong(event.getServletContext().getInitParameter("timerDelay"));
                    } catch (NumberFormatException numberformatexception) {
                    }
                if (log.isDebugEnabled()) {
                    System.out.println("Log4jListener: background watchdog thread delay: " + timerDelay);
                    log.debug("Log4jListener: background watchdog thread delay: " + timerDelay);
                }
                long timerPeriod = 5000L;
                if (event.getServletContext().getInitParameter("timerPeriod") != null)
                    try {
                        timerPeriod = Long.parseLong(event.getServletContext().getInitParameter("timerPeriod"));
                    } catch (NumberFormatException ignored) {
                    }
                if (log.isDebugEnabled()) {
                    System.out.println("Log4jListener: background watchdog thread period: " + timerPeriod);
                    log.debug("Log4jListener: background watchdog thread period: " + timerPeriod);
                }
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log4jListener.log.trace(Log4jListener.this);
                    }

                }, timerDelay);
            }
        } else {
            System.out.println("Log4jListener: Unable to configure logging subsystem!");
        }
    }

    private void configure(URI defaultLocation) {
        LoggerContext context = (LoggerContext) LogManager.getContext();

        if (location != null && location.exists()) {
            if (location.lastModified() != lastConfigure) {
                context.setConfigLocation(location.toURI());
                log.info("Logging configured from " + location.getAbsolutePath());
                lastConfigure = location.lastModified();
            }
        } else if (lastConfigure != 0L) {
            lastConfigure = 0L;
            context.setConfigLocation(defaultLocation);
            log.info("Logging configured from " + defaultLocation + " (default)");
        }
    }

    private URI getUri(String location) {
        try {
            return new URI(location);
        } catch (URISyntaxException e) {
            System.out.println(System.out.printf("Log4jListener: Unable to find configuration file %s!", location));
            return null;
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
        log.info("Shutting down...");
        System.out.println("Log4jListener: Stopping background watchdog thread...");
        timer.cancel();
    }

}
