package com.jhs.http;

import java.io.IOException;

import org.slf4j.*;
import com.jhs.http.config.Configuration;
import com.jhs.http.config.ConfigurationManager;
import com.jhs.http.core.ServerListenerThread;

/*
*   Driver pour le serveur HTTP
*/

public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    public static void main(String[] args) {
    
        LOGGER.info("Demarrage du Serveur HTTP...");
        ConfigurationManager.getInstance().loadConfiguration("src/main/resources/http.json");
        Configuration configuration = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("port " + configuration.getPort());
        LOGGER.info("Repertoire racine web: " + configuration.getWebRoot());

        try {

            ServerListenerThread serverListenerThread= new ServerListenerThread(configuration.getPort(), configuration.getWebRoot());
            serverListenerThread.start();

        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }
}
