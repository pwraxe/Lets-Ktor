package com.codexdroid

import TokenManager
import com.auth0.jwt.JWT
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.ktorm.database.Database

/**
 * Steps to Create JWT Token
 *
 * Step 1 : add dependency
 *      implementation "io.ktor:ktor-auth:$ktor_version" //1.6.6
 *      implementation "io.ktor:ktor-auth-jwt:$ktor_version"  //1.6.6
 *
 * Step 2 :
 *      create 'application.conf' file in resource directory
 *      if file already exists then add following line init
 *
 *              secret = "key_@100796_akshay"               # any secrete Key
 *              issuer = "http://localhost:8080/"           # or can be > http://0.0.0.0:88080/
 *              audience = "http://localhost:8080/view"     # set to default URL
 *              realm = "Access to 'view'"
 *
 * Step 3 :
 *      Create Class 'TokenManager'  create fun to get token
 *
 * Step 4 :
 *      Perform   dataRouting()  function as per your requirement
 *
 *
 *
 * */

@Serializable
data class UserData(var id:Int, var username: String, var password:String)


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    install(ContentNegotiation){
        json(Json {
            this.prettyPrint = true
            this.isLenient = true
        })
    }

    install(Authentication){
        jwt {  }
    }

    routing {
        dataRouting()
    }
}


fun Routing.dataRouting(){

    val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))

    post("/token") {
        val userData = call.receive<UserData>()

        val token = tokenManager.generateJwt(userData)
        call.respond("Token : $token")
    }
}