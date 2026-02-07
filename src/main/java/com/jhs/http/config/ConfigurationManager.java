package com.jhs.http.config;

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
    public void loadConfiguration(String filePath){
    
    }

    public void getCurrentConfiguration() {

    }
}