package com.jhs.http.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerListenerThread extends Thread{
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private int port;
    private String webRoot;
    private ServerSocket serverSocket;
    
    public ServerListenerThread(int port, String webRoot) throws IOException {
        this.port = port;
        this.webRoot = webRoot;
        this.serverSocket = new ServerSocket(this.port);
    }

    @Override
    public void run(){
        try {

            
            Socket socket = serverSocket.accept();

            LOGGER.info("Nouvelle connexion entrante : " + socket.getInetAddress().getHostAddress());

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream(); 

            // TODO Lecture de la requete HTTP et generation de la reponse HTTP

            //TODO Ecriture de la reponse HTTP dans le outputStream

            String html = "<html><title>Simple http page</title><body><h1>Bonjour, Mode</h1></body></html>";

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
