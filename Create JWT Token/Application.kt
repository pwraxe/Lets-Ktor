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
 *--------------------------------------------------------- Login With Token ----------------------------------------------------------------
 *
 * Step 5 :
 *      Create another function in TokenManager as VerifyToken()
 *
 * Step 6 : rearrange in module > 1. install(Authentication) ; install(ContentNegotiation) ; routing  fun's
 *
 * */

@Serializable
data class UserData(var id:Int, var username: String, var password:String)

val config = HoconApplicationConfig(ConfigFactory.load())
val tokenManager = TokenManager(config)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

    install(Authentication){
        jwt {
            verifier(tokenManager.verifyToken())
            realm = config.property("realm").getString()
            validate { jwtCredential ->
                if(jwtCredential.payload.getClaim("username").asString().isNotEmpty()){
                    JWTPrincipal(jwtCredential.payload)
                }else{
                    null
                }
            }
        }
    }


    install(ContentNegotiation){
        json(Json {
            this.prettyPrint = true
            this.isLenient = true
        })
    }



    routing {
        dataRouting()
    }
}


fun Routing.dataRouting(){

    authenticate {
        get("/auth"){
            val principal = call.principal<JWTPrincipal>()
            val username = principal?.payload?.getClaim("username")?.asString()
            val password = principal?.payload?.getClaim("password")?.asString()
            call.respond("Hello $username / $password")
        }
    }

    post("/token") {
        val userData = call.receive<UserData>()
        val token = tokenManager.generateJwt(userData)
        call.respond("Token : $token")
    }
}
