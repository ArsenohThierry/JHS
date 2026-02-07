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

    => Ajout de HttpConnectionWorkerThread.java : 

    ServerListenerThread.java ne fait qu'accepter les connections pour avoir un socket particulier 

    puis on le passe a "un nouveau" HttpConnectionWorkerThread.java qui lui va gerer les messages , retourner les reponses ,pages , ... bref le job de tout a l'heure en mono-thread
    => a chaque connection , on envoie a un nouveau HttpConnectionWorkerThread => plusieurs theads peuxt fonctionner en meme temps : <Multi-threading> 
    
    !OK!
    Juste il faut fermer les scoket a la fin ! et c'est ok


    Partie 5 : implementation du protocole http

    Le protocole http est defini par des RFC deja implementes par le createur des http protocol , html ,... Documentaion necessaire ,...
    Mais on utilisera que RFC7230 : HTTP/1.1 (Message syntax and routing)

    RFC7231 : HTTP/1.1 (Semantics and contexts)

    Depuis les documentations : 
    Format des messages :
    -Encodage : USASCII
    
    -HTTP-message = start-line
    *< header-field CRLF >
    CRLF
    [ message-body ]

    EXPLICATION CHAT (resume de comment fonctionne les message de requetes http ):
    Entre 3:26 et 7:14, la vidéo explique comment analyser et structurer une requête HTTP reçue par un serveur Java, en s’appuyant sur les RFC officielles du protocole HTTP.​​

1. Ce que le serveur reçoit
À partir de 3:26, l’auteur montre comment récupérer la requête brute envoyée par le navigateur (via l’InputStream du socket) et l’afficher caractère par caractère.​
Il copie ensuite cette requête dans un fichier texte (request.txt) pour l’utiliser comme exemple concret de requête HTTP à parser plus tard.​

2. Vue d’ensemble du format HTTP (RFC 7230)
À partir de 4:00 environ, il ouvre la RFC 7230 et explique la structure générale d’un message HTTP :

un start line (ligne de départ),

zéro ou plusieurs champs d’en‑tête (header fields),

un body optionnel.​

Il insiste sur le fait que le body n’est pas obligatoire et que la séparation entre les en‑têtes et le body se fait via un saut de ligne spécial (CRLF).​

3. Découpage de la ligne de requête
Vers 5:00–6:00, il se concentre sur la request line (la première ligne de la requête), qui est définie ainsi dans la RFC :

M
e
ˊ
thode
  
espace
  
Request‑target
  
espace
  
HTTP‑version
  
CRLF
M 
e
ˊ
 thodeespaceRequest‑targetespaceHTTP‑versionCRLF
Il montre que la méthode est un token (par exemple GET, POST, etc.) et qu’elle doit être en majuscules par convention, et que si la méthode n’est pas reconnue, le serveur doit répondre par un code 501 Not Implemented.​

4. Choix de la stratégie de parsing
Vers 6:30–7:14, il explique qu’il va implémenter un parser « scanner‑less » (sans tokenizer) :

au lieu de découper d’abord tout le flux en tokens, le parser lit les caractères au fur et à mesure,

cela permet de détecter rapidement les requêtes mal formées ou malveillantes et de fermer la connexion si besoin.​

Il termine cette plage en préparant le terrain pour la suite : il crée un package http, une classe HttpParser et une méthode parseHttpRequest(InputStream), puis met en place un test JUnit pour tester le parser avec la requête exemple.​

Si tu veux, je peux te détailler ligne par ligne ce que fait le code entre 6:30 et 7:14 (création de la classe, du test, du ByteArrayInputStream, etc.).