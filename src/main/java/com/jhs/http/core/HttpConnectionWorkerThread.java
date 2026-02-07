package com.jhs.http.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectionWorkerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private final Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = this.socket.getInputStream();
            outputStream = this.socket.getOutputStream();

            // TODO Lecture de la requete HTTP et generation de la reponse HTTP

            // TODO Ecriture de la reponse HTTP dans le outputStream

            String html = "<html><title>Simple http page</title><body><h1>Bonjour, Mode</h1></body></html>";

            final String CRLF = "\r\n"; // 13, 10

            String response = "HTTP/1.1 200 OK" + CRLF + // ligne de statut
                    "Content-length: " + html.getBytes().length + CRLF + // entete de la reponse (header)
                    CRLF + CRLF +
                    html;

            outputStream.write(response.getBytes());

            LOGGER.info("Connexion traitée et fermée...");

        } catch (IOException ex) {
            System.getLogger(HttpConnectionWorkerThread.class.getName()).log(System.Logger.Level.ERROR, (String) null,
                    ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Erreur lors de la fermeture du flux d'entrée", e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Erreur lors de la fermeture du flux de sortie", e);
                }
            }

            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                LOGGER.error("Erreur lors de la fermeture du socket", e);
            }
        }
    }
}
