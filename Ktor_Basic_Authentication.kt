package com.codexdroid

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Authentication) {
        basic("axeAuth") {
            realm = "Ktor Server"
            validate { if (it.name == "test" && it.password == "1234") UserIdPrincipal(it.name) else null }
        }
    }

    install(ContentNegotiation) {
        gson {}
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }


        //authenticate user with username and pasword before accessing this api
        authenticate("axeAuth") {
            get("/user/validate") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }

        get("/tojson") {
            call.respond(mapOf("name" to "Akshay"))
        }
    }
}

