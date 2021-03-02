fun Application.module(){ 

      install(StatusPages) {
        
          exception<Exception> { ex ->
              call.respond("ex : ${ex}")
          }
      }

      routing {
        
         get("/") {
              var name : String? = null
              val size = name?.length ?: throw NullPointerException("its an empty string")
              call.respondText(name.length.toString())
         }
      }
}
