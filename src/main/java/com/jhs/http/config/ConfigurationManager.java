package com.jhs.http.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.jhs.http.utils.Json;

/*
* Cette classe va etre un singleton car on n'a besoin que d'une seule instance de 
* configuration pour le serveur HTTP. Elle va lire les paramètres de configuration à partir d'un fichier 
* JSON (http.json) et fournir des méthodes pour accéder à ces paramètres.
*/
public class ConfigurationManager {

    private static ConfigurationManager maConfigurationManager;
    private Configuration configurationActuelle;

    public ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if (maConfigurationManager == null) {
            maConfigurationManager = new ConfigurationManager();
        }
        return maConfigurationManager;
    }

    /*
     * Cette méthode va lire les paramètres de configuration à partir du fichier
     * http.json
     */
    public void loadConfiguration(String filePath) {
        FileReader filereader = null;
        try {
            filereader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new HttpConfigurationException("Fichier de configuration introuvable : " + filePath, e);
        }
        finally{
            if (filereader != null) {
                try {
                    filereader.close();
                } catch (IOException e) {
                    throw new HttpConfigurationException("Erreur lors de la fermeture du fichier de configuration", e);
                }
            }
        }
        
        StringBuilder stringbuilder = new StringBuilder();
        int i = 0;
        try {
            while ((i = filereader.read()) != -1) {
                stringbuilder.append((char) i);
            }
        } catch (IOException e) {
            throw new HttpConfigurationException(e);
        }
        JsonNode configuration;
        try {
            configuration = Json.parse(stringbuilder.toString());
        } catch (Exception e) {
            throw new HttpConfigurationException("Erreur lors du parsing de la configuration JSON", e);
        }
        try {
            configurationActuelle = Json.fromJson(configuration, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new HttpConfigurationException("Erreur lors de la conversion de la configuration JSON", e);
        }
    }

    public Configuration getCurrentConfiguration() {
        if (configurationActuelle == null) {
            throw new HttpConfigurationException("Aucune configuration mis en place...");
        }
        return configurationActuelle;
    }
}