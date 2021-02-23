package com.codexdroid

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val personList = mutableListOf(
    Person(100,"Akshay","pawar@gmail.com",9696969696),
    Person(501,"Cherry","alex@gmail.co",9988776655),
    Person(302,"Villan","alan@gmail.com",8888888888),
    Person(850,"Kiran","kiran@gmail.com",7777777777),
    Person(100,"Amruta","amruta@gmail.com",7766554433))

@Suppress("unused") // Referenced in application.conf
fun Application.module() {



    install(ContentNegotiation){
        json(Json {
            this.isLenient = true
            this.prettyPrint = true
        })
    }


    //Method 1 for Routing
    routing {

        //share all data
        get("/person") {
            if(personList.isNotEmpty()){
                call.respond(personList)
            }else{
                call.respondText("No Data at Server",status = HttpStatusCode.NotFound)
            }
        }

        //share only that data which match id
        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText("No Given Id Found", status = HttpStatusCode.NotFound)
            val thatPerson = personList.find{ it.id == id.toInt() } ?: call.respondText("No Person Found",status = HttpStatusCode.NotFound)
            call.respond(thatPerson)
        }


        //get nested data
        get("/person/{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText("No Given Id Found", status = HttpStatusCode.NotFound)
            val thatPerson = personList.find { it.id == id.toInt() } ?: call.respondText("No Person Found",status = HttpStatusCode.NotFound)
            call.respond(thatPerson)
        }

        //receives data from client
        post {
            val person = call.receive<Person>()
            personList.add(person)
            call.respondText("Data Added Successfully",status = HttpStatusCode.Accepted)
        }

        //delete specific data which match id
        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respondText("No Id Found For delete data")
            personList.removeIf { it.id == id.toInt() }
            call.respondText("Data Deleted Successfully",status = HttpStatusCode.Accepted)
        }

    }

    //Method 2 for Routing
    routing {
        routeForPerson()
    }


}


private fun Route.routeForPerson(){

    route("/human"){

        get {
            if(personList.isNotEmpty()){
                call.respond(personList)
            }else{
                call.respondText("No Data at Server",status = HttpStatusCode.NotFound)
            }
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText("No Given Id Found", status = HttpStatusCode.NotFound)
            val thatPerson = personList.find { it.id == id.toInt() } ?: call.respondText("No Person Found",status = HttpStatusCode.NotFound)
            call.respond(thatPerson)
        }

        post {
            val person = call.receive<Person>()
            personList.add(person)
            call.respondText("Data Added Successfully",status = HttpStatusCode.Accepted)
        }

        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respondText("No Id Found For delete data")
            personList.removeIf { it.id == id.toInt() }
            call.respondText("Data Deleted Successfully",status = HttpStatusCode.Accepted)
        }
    }

    //we can have multiple routes like this
    route("/men"){
        get("/"){}
    }
}
