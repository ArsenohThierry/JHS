package com.jhs.http;

import com.jhs.http.config.ConfigurationManager;

/*
*   Driver pour le serveur HTTP
*/

public class HttpServer {
    public static void main(String[] args) {
        System.out.println("Starting HTTP Server...");
        
        ConfigurationManager.getInstance().loadConfiguration("src/main/resources/http.json");
    }
}
