//hello world in two way

when you build and run you will get 0.0.0.0:8080 url
if it not work then go localhost:8080

------------------------- Method : 1 -------------------------

fun main() {
    embeddedServer(Netty,8080){
        routing {
            get("/"){
                call.respondText("Hello from Method 1", ContentType.Application.Json)
            }
        }
    }.start(true)
}
------------------------- Method : 2 -------------------------

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {

    routing {
        get("/"){
            call.respondText("I am from method 2",ContentType.Application.Json)
        }
        get("/bio"){
            call.respondText("Hello I am Akshay")
        }
    }
}
