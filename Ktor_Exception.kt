fun Application.module(){ 

      install(StatusPages) {
        
          exception<AuthenticationException> { cause ->
               call.respond(HttpStatusCode.Unauthorized)
          }
            
          exception<AuthorizationException> { cause ->
               call.respond(HttpStatusCode.Forbidden)
          }
            
          exception<Exception> { ex ->
              call.respond("ex : ${ex}")
          }
           ....
            //can have multiple exeption handle code here
            
      }

      routing {
        
         get("/") {
              var name : String? = null
              val size = name?.length ?: throw NullPointerException("its an empty string")
              call.respondText(name.length.toString())
         }
      }
}

______________________________________________________________________________________________________________________


