- HTTP/2 -> It is BDMP(Binary Duplex Multiplexing Protocol) & Replacement of HTTP/1.x
- To Create Server there are two approch 1) embeddedServer   2) EngineMain
- in Routing we can have multiple get, post, put, delete method with different param
- We can have nested route method in routing
- whatever data we responding to client then content type is important it shows what type of data you sending

Selecting Feature on New Project (!4All)
Server 
  - Feature
      - Static Content    : Server Static files from define locations
      - Locations         : Allows to define route locations in a typed way
      - AutoHeadResponse  : Provide responses to HEAD request for existing route that have GET verb define
      - CallLogging       : Logs client requests
      - DataConversion    : Allows to serialized and deserialized list of valies
      - DefaultHeader     : adds default set of header to HTTP
      - StatusPages       : Allows to respond to thown exceptions
      - Routing           : Allows to define structure route and associated handlers

  - Authentications
      - Authentication Basic  : Handle Basic Authentications
      - Authentication        : Handle Basic and Digest HTTP Auth, from authentication and OAuth

  - Content Negotiation
      - GSON                  : Handle JSON Serialization using GSON library
      - ContentNegotiation    : Provides auto content conversions accroding to Content-Type and Accept Headers 
===================================================

Client
   - HttpClient Engine
      - HttpClientEngine      : Core of HTTP client require for lib.
      - Apache Http Clinet    : Engine for ktor HttpClient using Apache, Supports HTTPS 1.x  and HTTP 2.x

    - Feature
      - Auth Feature HttpClient : Supports auth for HttpClient
      - Logging Feature         : Logging Feature for debugging client calls
  
  


