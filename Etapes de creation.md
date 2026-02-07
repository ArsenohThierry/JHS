-Requis : 
    -Read Configuration file
    -Ouvrir un socket sur le port spécifié (peut gerer le multi-threading)
    -Lire les requetes HTTP entrantes
    -Traiter les requetes (GET, POST, etc.)
    -Ouvrir les fichiers demandés et les envoyer en réponse
    -Ecrire les responses HTTP appropriées (headers, status codes, etc.)
    -Gérer les erreurs (404, 500, etc.)
    -Fermer les connexions après le traitement des requetes

Comment?:
    -Ecrire les Configs en json:
        -Utilisation de Jackson pour transformer les Json en POJO (JSON -> classe Java)
 



    Classes existantes :

    - `com.jhs.http.HttpServer` : programme principal (driver) du serveur HTTP. Démarre le serveur et charge la configuration via `ConfigurationManager`.

    - `com.jhs.http.config.ConfigurationManager` : singleton chargé de lire le fichier JSON de configuration (`src/main/resources/http.json`), de le parser avec l'utilitaire `Json` et de fournir l'objet `Configuration` courant.

    - `com.jhs.http.config.Configuration` : POJO de configuration contenant les paramètres essentiels (ex. `port`, `webRoot`). Utilisé comme cible de désérialisation JSON.

    - `com.jhs.http.config.HttpConfigurationException` : exception runtime spécifique aux erreurs de configuration (fichier manquant, parsing, etc.).

    - `com.jhs.http.utils.Json` : utilitaire central pour la sérialisation/désérialisation JSON (basé sur Jackson). Fournit `parse`, `fromJson`, `toJson` et des méthodes de formattage (`stringifier`, `stringifierMeilleur`).


Pour faire fonctionner le Serveur :
    1- Capable de handle les connexions TCP (pour envoyer les pages aux clients )
    2- Comprendre le protocole HTTP

1 - TCP connections :
    Etapes : 
    1- : Seulement 1 connectoin a la fois (mono-threading)
    2- Pas encore de protocole http implemente 
    3- Toujours encoder en HTML pour l instant : (php plus tard)

    1.1- Utilisation de ServerSocket : pour pouvoir ecouter a un port specifique, ouvrir le port (serverSocket.accept();) et le passer a un socket
    Si aucune connexion ne st etablie socket.accept ne s'execute pas et attend une connexion 

    Pour envoyer une page par exemple :

    String html = "<html><title>Simple http page</title><body><h1>Bonjour, Monde!</h1></body></html>";

    au navigateur , le navigateur ne saura pas quoi en faire du coup il faut trouver un moyen de dire au navigateur de l encoder dans un certain format : 
    => l40- 55 

    Mais on va gerer les socket dans un thread pour un meilleur gestion du projet 
    et gerer le multi-threading (accepter plusieurs connexions )

PART 4 : multi-thrading
    -Creation du package core et de la classe ServerListenerThread pour gerer le multithreading : 
    On fera en sorte que le serveur puisses ecouter sur plusieurs ports et avoir plusieurs webroot (dossiers)

    J'ai deplace la fonction de demarrage du serveur (qui ecoute sur 1 port) dans la nouvelle classe et implemente la fonction run() du thread pour demarrer le serveur (tjrs mono-thread pour l instant)

    Ajout d une autre dependance (sl4j) pour la journalisation : (erreurs, succes bref logs) por remplacer sout

    Maintenant gestion De plusieurs connexions enmeme temps :MULTI-THREADING

    -ajout d'une boucle while pour ne pas arreter l ecoute :
    MAIS : meme si le programme ne finit pas , le serveur n' accepte qu' une requete a la fois (pas du tout optimal)