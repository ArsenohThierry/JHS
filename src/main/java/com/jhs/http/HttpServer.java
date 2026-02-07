package com.jhs.http;

import com.jhs.http.config.Configuration;
import com.jhs.http.config.ConfigurationManager;

/*
*   Driver pour le serveur HTTP
*/

public class HttpServer {
    public static void main(String[] args) {
        System.out.println("Demarrage du Serveur HTTP...");

        ConfigurationManager.getInstance().loadConfiguration("src/main/resources/http.json");
        Configuration configuration = ConfigurationManager.getInstance().getCurrentConfiguration();

        System.out.println("port " + configuration.getPort());
        System.out.println("Repertoire racine web: " + configuration.getWebRoot());
    }
}
