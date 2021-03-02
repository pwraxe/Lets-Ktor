routing{

        get("/") {
          call.respond("Hello Akshay")  //Hello Akshay
          call.respond(HttpStatusCode.Accepted,"Hello Akshay") //Hello Akshay
          
          //we can see these custom header at , browser > inspect element > network tab > Headers
          call.response.header("X_TIME",1000L)
          call.response.header("Connection","Offline")
          
          call.response.etag("33a64df551425fcc55e4d42a148795d9f25f89d4")
          call.response.lastModified(ZonedDateTime.now())
          
          //if we write togather call.respondText fun then
          call.respondText { "Hello Akshay 1 " }  // this work like return statement
          call.respondText("Hello Akshay 2 ") // this code won't execute
          


        }
