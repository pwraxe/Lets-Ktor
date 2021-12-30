/****

To View Database , Tables, DataIn Table 
We need to connect remote database using digital ocean credentials in either command promp, Terminal 

Then fire queries of mysql in terminal 

**/




package com.codexdroid

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import kotlinx.serialization.Serializable
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import kotlin.random.Random

private var database: Database? = null

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)




//Create table for inserting data into database
object PersonTable : Table<Nothing>(tableName = "person"){          // This class define to send data to database // 'person' is tablename define/created in  phpmyadmin
    val id = int("id").primaryKey()
    val name = varchar("name")
    val email = varchar("email")
    val mobile = varchar("mobile")
}

fun getDatabase() : Database? {
    database = if(database == null){
        Database.connect(
            url = "jdbc:hostname.com:port/dabasename"
            driver = "com.mysql.cj.jdbc.Driver",
            user = "username",  //same like root
            password = "password")
    }else database
    return database
}


@Serializable
data class Person(var id:Int,var name: String,var email:String,var mobile:String)

@Serializable
data class UserData(var status: Int, var message: String, var person : MutableList<Person>)

val personList = mutableListOf<Person>()

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(ContentNegotiation) {
        gson {}
    }

    routing {
        get("/username/{name}"){
            val name = call.parameters["name"] ?: call.respond(HttpStatusCode.BadRequest,"Enter String only")
            call.respond(HttpStatusCode.OK,"Hello $name, How are you?")
        }

        get("/email/{email}"){
            val email = call.parameters["email"] ?: call.respond(HttpStatusCode.BadRequest,"Enter String only")
            call.respond(HttpStatusCode.OK,"Hello $email, We mail you Secret Key, Please Delete It with in 1 Second ")
        }

        get("/info"){
            call.respond("No Information available for now, pls keep visiting")
        }

        get("/"){
            Random.nextInt()
            var fname = ""
            var lname = ""
            repeat(6){
                fname += Random.nextInt(65,65+26).toChar().toString()
                lname += Random.nextInt(97,97+26).toChar().toString()
            }
            personList.add(Person(Random.nextInt(100,999),"$fname $lname","${fname}@codexdroid.com",Random.nextLong(1111111111,9999999999).toString()))
            call.respond(UserData(HttpStatusCode.OK.value,"Here's Your Data", personList))
        }
        
        //------------------------------------------------REAL TIME CRUD OPERATION ON DIGITAL OCEAN DATABASE-----------------------------------------------------------------------

        post("/data"){
            val person = call.receive<Person>()

            println("====> ${call.request.authorization()}")  //Bearer Token

            val affectedRows = getDatabase()?.insert(PersonTable){
                set(PersonTable.id,person.id)
                set(PersonTable.name,person.name)
                set(PersonTable.email,person.email)
                set(PersonTable.mobile,person.mobile)
            }

            if(affectedRows == 1){
                call.respond("Data inserted")
            }else{
                call.respond("Data Could not insert")
            }
        }

        get("/view"){
            personList.clear()
            val query = getDatabase()?.from(PersonTable)?.select()
            for (row in query!!){
                personList.add(Person(row[PersonTable.id]!!,row[PersonTable.name]!!, row[PersonTable.email]!!, row[PersonTable.mobile]!!))
            }
            call.respond(UserData(HttpStatusCode.OK.value,"Data Retrieved Successfully",personList))
        }

        put("/update_object"){
            val person = call.receive<Person>()
            val affectedRows = getDatabase()?.update(PersonTable){
                set(PersonTable.name,person.name)
                set(PersonTable.email,person.email)
                set(PersonTable.mobile,person.mobile)
                where { PersonTable.id eq person.id }
            }

            if(affectedRows!! > 0){
                call.respond("Data Successfully Updated")
            }else{
                call.respond("Error to Updated Data")
            }
        }

        put("/update_id/{id}"){
            val id = call.parameters["id"].toString().toInt();
            val person = call.receive<Person>()

            val affectedRows = getDatabase()?.update(PersonTable){
                set(PersonTable.name,person.name)
                set(PersonTable.email,person.email)
                set(PersonTable.mobile,person.mobile)
                where { PersonTable.id eq id }
            }

            if(affectedRows!! > 0){
                call.respond("Data Successfully Updated of id = $id")
            }else{
                call.respond("Error to Updated Data")
            }
        }

        delete("/delete_object"){
            val person = call.receive<Person>()
            val affectedRows = getDatabase()?.delete(PersonTable){
                PersonTable.id eq person.id
            }
            if(affectedRows!! > 0){
                call.respond("Data Successfully Deleted")
            }else{
                call.respond("Error to Delete Data")
            }
        }

        delete("/delete_id/{id}"){
            val id = call.parameters["id"].toString().toInt();
            val affectedRows = getDatabase()?.delete(PersonTable){
                PersonTable.id eq id
            }

            if(affectedRows!! > 0){
                call.respond("Data Successfully Deleted")
            }else{
                call.respond("Error to Delete Data")
            }
        }
    }
}


