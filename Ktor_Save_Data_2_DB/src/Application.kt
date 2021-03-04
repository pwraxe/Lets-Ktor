package com.codexdroid

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.content.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.select
import org.ktorm.entity.sequenceOf
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Statement
import javax.sql.DataSource


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {

    val url = "jdbc:mysql://localhost/test"
    val user = "root"
    val pass = ""
    val insertQuery = "INSERT INTO human VALUES(?,?,?,?)"

    var dataConnect : Database
    lateinit var person : Person

    install(ContentNegotiation){
        json(Json {
            this.prettyPrint = true
            this.isLenient = true
        })
    }


    routing {


        //Get Data from Client and store it in DB

        post{

            person = call.receive<Person>()

            try {

                dataConnect = Database.connect(url = url, driver = "com.mysql.jdbc.Driver", user, pass)
                dataConnect.useTransaction {
                    dataConnect.insert(Human){
                        set(it.id,person.id)
                        set(it.name,person.name)
                        set(it.email,person.email)
                        set(it.mobile,person.mobile)
                    }
                }

                call.respond(SuccessResponse(200,"Data Inserted",true))

            }catch (ex : Exception){
                call.respond(FailResponse(422,"Error to Insert : $ex",false))
            }
        }
    }
}