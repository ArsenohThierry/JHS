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
        (ConfigurationManager.java, Configuration.java, HttpConfigurationException.java, Json.java)
 



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
    (HttpServer.java, ServerListenerThread.java, HttpConnectionWorkerThread.java)

PART 4 : multi-thrading
    -Creation du package core et de la classe ServerListenerThread pour gerer le multithreading : 
    On fera en sorte que le serveur puisses ecouter sur plusieurs ports et avoir plusieurs webroot (dossiers)

    J'ai deplace la fonction de demarrage du serveur (qui ecoute sur 1 port) dans la nouvelle classe et implemente la fonction run() du thread pour demarrer le serveur (tjrs mono-thread pour l instant)

    Ajout d une autre dependance (sl4j) pour la journalisation : (erreurs, succes bref logs) por remplacer sout

    Maintenant gestion De plusieurs connexions enmeme temps :MULTI-THREADING
    (ServerListenerThread.java, HttpConnectionWorkerThread.java)

    -ajout d'une boucle while pour ne pas arreter l ecoute :
    MAIS : meme si le programme ne finit pas , le serveur n' accepte qu' une requete a la fois (pas du tout optimal)

    => Ajout de HttpConnectionWorkerThread.java : 

    ServerListenerThread.java ne fait qu'accepter les connections pour avoir un socket particulier 

    puis on le passe a "un nouveau" HttpConnectionWorkerThread.java qui lui va gerer les messages , retourner les reponses ,pages , ... bref le job de tout a l'heure en mono-thread
    => a chaque connection , on envoie a un nouveau HttpConnectionWorkerThread => plusieurs theads peuxt fonctionner en meme temps : <Multi-threading> 
    
    !OK!
    Juste il faut fermer les scoket a la fin ! et c'est ok


    Partie 5 : implementation du protocole http

    (HttpParser.java, HttpRequest.java, HttpMessage.java, HttpMethod.java, HttpStatusCode.java, HttpParsingException.java)

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



  ______________
 | HTTP Message | :
 ‾‾‾‾‾‾‾‾‾‾‾‾‾‾
 ┌─────────────┐
 │ START-LINE  │   ----
 └─────────────┘       | 
        |              |
        v              |
 ┌─────────────┐       |
 │ HEADER-FIELD│   -----
 └─────────────┘       |
        +              |
                       |
        |              |
        v              | 
    ┌──────┐           |
    │ CRLF │           |
    └──────┘           |
        |              |
        v              |
    ┌──────┐           |
    │ CRLF │-----------|
    └──────┘           |
        |              |
        v              |
 ┌─────────────┐       |
 │ MESSAGE-BODY│-------|
 └─────────────┘       |
        |              |
        v              |
 ┌─────────────┐       |
 │ END         |-------|
 └─────────────┘

 -Start line : Peux contenir :
                -Ligne de requetes, (request lines) : // method _espace_ request-target _espace_ HTTP-version CRLF \\ Methode: token , methodes qui fonctionnent : GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE , PATCH
                -Ligne de status    (status lines)

Maintenant on a besoin de parser ces messages pour pouvoir les comprendre et les traiter : 
    <!-- -Creation de HttpParser.java : 
    -Creation de la methode parseHttpRequest(InputStream) : qui va parser la requete http et retourner un objet HttpRequest (classe a creer) qui va representer la requete http de maniere structurée (avec des champs pour la methode, le path, les headers, etc.)
    -Creation de HttpRequest.java : POJO pour representer une requete http avec des champs pour la methode, le path, les headers, etc.
    -Creation de HttpParserTest.java : test JUnit pour tester le parser avec la requete exemple (request.txt)                 -->

    Methodes de parsing : 1 - Lexer (Tokenizer) + parser :
                        -On lit tout le flux d un coup , on le tokenize (decoupage en tokens) puis on parse les tokens pour construire l objet HttpRequest
                        -Avantage : plus rapide pour les requetes bien formées
                        -Inconvenient : plus lent pour les requetes mal formées ou malveillantes (car on doit lire tout le flux avant de detecter l erreur)

                        2- Lexerless pareser (Scannerless parser) :
                        -On lit les caracteres un par un et on parse au fur et a mesure
                        -Avantage : plus rapide pour les requetes mal formées ou malveillantes (car on peut detecter l erreur rapidement et fermer la connexion)
                        -Inconvenient : plus lent pour les requetes bien formées (car on doit lire les caracteres un par un 

    Par convention , on utilise la methode 2 : lexerless parser (scannerless parser) pour pouvoir detecter rapidement les requetes mal formées ou malveillantes et fermer la connexion si besoin

    --->
    Ajout d'une nouvelle dependance : JUnit pour les tests unitaires : 
        -Creation de HttpParserTest.java : test JUnit pour tester le parser avec la requete exemple (request.txt)
        (HttpParserTest.java)

Partie 6 :

Maintenant on va parser les requetes http et les traiter : 
    -parser les headers ,
    -parser le body (si present)
    -gerer les differentes methodes (GET, POST, etc.)
NB: On n utilise pas directement le inputstream on va utiliser InputStreamHandler
    
    <!-- -Creation de HttpRequestHandler.java : classe pour traiter les requetes http (GET, POST, etc.) et retourner les reponses appropriées (pages, codes d erreur, etc.)
    -Modification de HttpConnectionWorkerThread.java : pour utiliser HttpRequestHandler pour traiter les requetes et retourner les reponses -->

REGLES GENERALES pour les headers en http : 
    -Les headers sont case-insensitive (ex: Content-Type et content-type sont equivalents)
    -Les methodes GET et HEAD doivent imperativent etre implementées (si une methode n est pas reconnue , le serveur doit repondre par un code 501 Not Implemented)
    -Si la methode est reconnue mais non autorisée pour la ressource demandée , le serveur doit repondre par un code 405 Method Not Allowed
    -Le body est optionnel (ex: une requete GET n a pas de body, alors qu une requete POST peut en avoir un)
    -La separation entre les headers et le body se fait via un saut de ligne special (CRLF) : \r\n\r\n


    -Request Line : 
        -les requetes invalides doivent etre traitees avec un code d erreur 400 Bad Request ou 301 Moved Permanently (si la ressource a ete deplacee)
        -Si la requete est tres longue (ex: un header tres long) , le serveur doit repondre par un code 501 not implemented ou 414 Request-URI Too Long si c'est plus long qu nn URI
        -Reccommande que les envoyers et recipients supportent des lignes de requete d au moins 8000 octets (8KB) pour eviter les erreurs de requetes trop longues

COMMENT ?:
-Pour le header : 
Utilisation du table ASCI pour parser les headers : 
    -Space = 20
    -CR = 13
    -LF = 10

(HttpParser.java)
Maintenant on va parcourir les headers pour cherches ces bytes correspondants    


Partie 7:
Suite des paring des headers : 
    -Parser les headers : on va parcourir les headers pour cherches ces bytes correspondants (space, CR, LF) pour pouvoir parser les headers et les stocker dans une map (key: header name, value: header value)
    -Parser le body (si present) : on va lire le body apres la separation CRLF CRLF et le stocker dans une string ou un byte array (en fonction du content-type)
    -Gerer les differentes methodes (GET, POST, etc.) : on va utiliser un switch case pour gerer les differentes methodes et retourner @Test
    public void testParseBadHttpRequest() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generatebadtestCaseMethodName());
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED);
        }

    }les reponses appropriees (pages, codes d erreur, etc.)


-Gerer les cas Pour des mauvais/incorrects requetes HTTP: 
(TENA LAVA LOTRA NEFA VO METHODE 1 ZAY (GET))
dans HttpParserTest.java


Part 8 :

Maintenant que le parsing est presque fini : 
    -Nous avons trouve le Request target mais on ne l' a pas encore assigne a la requete 

    creation de la methode setRequestTarget(String) dans HttpRequest.java pour assigner le request target a la requete (HttpRequest.java)

    -Ensuite la version de la requete :
    Plus complexe car la structuree des verion sont une peu complexe
        Forme : HTTP / DIGIT . DIGIT (2 NOMBRES SEPARES PAR UN POINT)
        Ex: HTTP/1.1
    Un serveur peut retourner un code d erreur 505 HTTP Version Not Supported si la version n est pas supportee ou reconnue


-PASRING DE LA VERSION HTTP : (Lava be ):
    -Creation de HttpVersion.java : classe pour representer la version HTTP d une requete (ex: HTTP/1.1)
    -Creation de la methode getBestCompatibleVersion(String) dans HttpVersion.java : pour parser la version HTTP a partir de la request line et retourner un objet HttpVersion representant la version de la requete
    -Modification de HttpParser.java : pour utiliser HttpVersion.getBestCompatibleVersion() pour parser la version HTTP de la requete et l assigner a l objet HttpRequest

Partie 9 :
Maintenant que le parsing du start line est fini : on va parser les headers 


Apres les methodes de parsing du headers : 
    -Creation de la methode parseHeaders(InputStreamReader, HttpRequest) dans HttpParser.java : pour parser les headers de la requete et les stocker dans l objet HttpRequest
    -Modification de HttpParserTest.java : pour tester le parsing des headers avec une requete exemple contenant des headers (request_with_headers.txt)

Mise en place d'une structure pour stocker les headers dans HttpRequest.java : 
    -Creation d une map (HashMap) pour stocker les headers (key: header name, value: header value)
    -Creation de la methode addHeader(String name, String value) dans HttpRequest.java pour ajouter un header a la requete    