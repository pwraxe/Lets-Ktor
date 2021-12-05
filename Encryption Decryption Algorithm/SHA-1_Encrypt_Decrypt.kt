
"SHA-1 is Single Way Encryption Algorithm hence you can encrypt text but cannot decrypt"

 Note : Make sure when encrypt password all instance are creating , 
 returning same instance will return same encrypted text/string for all different password/String/PlainText

package com.codexdroid

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.*
import java.sql.SQLIntegrityConstraintViolationException

//This is One-Way Encryption Algorithm
object SHA1Security{

    @Volatile private var messageDigest : MessageDigest? = null
    @Volatile private var digestBytes : ByteArray? = null
    @Volatile private var bigInteger :BigInteger? = null
    @Volatile private var encryptedText: StringBuilder? = null

    //When you call this method make sure each time instance create this will give you different encrypted text on different password
    // if you return same instance(Or applied singleton) then each time encrypted password will be same for all different password
    fun encryptPassword(password: String): String{

        messageDigest = MessageDigest.getInstance("SHA-1")
        digestBytes = messageDigest?.digest(password.toByteArray(StandardCharsets.UTF_8))
        bigInteger = BigInteger(1, digestBytes)
        encryptedText = StringBuilder(bigInteger?.toString(16))

        while (encryptedText?.length!! < 32){ encryptedText?.insert(0,'0') }
        return encryptedText.toString()
    }
}

@Serializable
data class Login(var id:Int,var username: String, var password: String)

@Serializable
data class CommonResponse(var statusCode: Int, var message:String, val data:MutableList<Login>?)

//Create table for inserting data into database
object LoginTable : Table<Nothing>(tableName = "login"){
    val id = int("id").primaryKey()
    val username = varchar("username")  //column name in database table 'login'
    val password = varchar("password") //column name in database table 'login'
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

private var database: Database? = null

fun Application.module() {

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
fun getDatabase() : Database? {
    database = if(database == null){
        Database.connect(
            url = "jdbc:mysql://localhost/test_ktorm",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = "")
    }else database
    return database
}

fun Routing.dataRouting(){

    val loginList = arrayListOf<Login>()

    post("/insert"){
        val login = call.receive<Login>()

        try {
            val affectedRows = getDatabase()?.insert(LoginTable){
                set(LoginTable.id,login.id)
                set(LoginTable.username,login.username)
                set(LoginTable.password,SHA1Security.encryptPassword(login.password))
            }

            if(affectedRows == 1){
                call.respond(CommonResponse(statusCode = HttpStatusCode.OK.value, "Data inserted", null))
            }else{
                call.respond(CommonResponse(statusCode = HttpStatusCode.BadRequest.value, "Error to insert Data", null))
            }
        }catch (ex : Exception){
            when (ex){
                is SQLIntegrityConstraintViolationException -> { call.respond(CommonResponse(statusCode = HttpStatusCode.Conflict.value, "ID ${login.id} already exists, pls try different", null))  }
            }
        }
    }

    post("/validate"){

        loginList.clear()
        val login = call.receive<Login>()
        val loginResponse = getDatabase()?.from(LoginTable)?.select()?.where{ LoginTable.id eq login.id }?.map {
            Login(
                id = it[LoginTable.id]!!,
                username = it[LoginTable.username]!!,
                //get client password encrypt it == get database password , if true return client plain text password else encrypted pass
                password = if (SHA1Security.encryptPassword(login.password) == it[LoginTable.password]!!) login.password else it[LoginTable.password]!!
            )
        }?.firstOrNull()

        loginResponse?.let {
            loginList.add(loginResponse)
        }


        if(loginList.isNotEmpty()){
            call.respond(
                CommonResponse(
                    statusCode = HttpStatusCode.OK.value,
                    message = "${loginList.size} Person(s) Found",
                    loginList
                )
            )
        }else{
            call.respond(
                CommonResponse(
                    statusCode = HttpStatusCode.NotFound.value,
                    message = "No Person(s) Found",
                    null
                )
            )
        }
    }
}
