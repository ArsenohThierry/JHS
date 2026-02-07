package com.jhs.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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

        try {

            ServerSocket serverSocket = new ServerSocket(configuration.getPort());
            Socket socket = serverSocket.accept();

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream(); 

            // TODO Lecture de la requete HTTP et generation de la reponse HTTP

            //TODO Ecriture de la reponse HTTP dans le outputStream

            String html = "<html><title>Simple http page</title><body><h1>Bonjour, Monde!</h1></body></html>";

            final String CRLF = "\r\n"; //13, 10

            String response = 
                "HTTP/1.1 200 OK" + CRLF + // ligne de statut 
                "Content-length: " + html.getBytes().length + CRLF + // entete de la reponse (header)  
                CRLF + CRLF +
                html;
                
            outputStream.write(response.getBytes());

            inputStream.close();
            outputStream.close();
            socket.close();
            serverSocket.close();
            
        } catch (IOException e) {
            
        }
    }
}
