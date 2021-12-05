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
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

//This is One-Way Encryption Algorithm
object AESSecurity{

    private const val SECRETE_KEY = "b2ff996be3df7adc70d1a3c2c0a95aab97b88301"
    private const val SALT = "u7lDkODaFVVQCx8FDdwBog=="
    private var byteArray = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    private val ivSpec = IvParameterSpec(byteArray)
    private val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    private val keySpec = PBEKeySpec(SECRETE_KEY.toCharArray(), SALT.toByteArray(),65535,256)
    private val tempSecreteKey = factory.generateSecret(keySpec)
    private val secreteKeySpec = SecretKeySpec(tempSecreteKey.encoded,"AES")
    private val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    fun encryptPassword(password: String) : String{
        cipher.init(Cipher.ENCRYPT_MODE,secreteKeySpec,ivSpec)
        return Base64.getEncoder().encodeToString(cipher.doFinal(password.toByteArray(StandardCharsets.UTF_8)))
    }

    fun decryptPassword(encryptedPassword: String) : String{
        cipher.init(Cipher.DECRYPT_MODE,secreteKeySpec,ivSpec)
        return String(cipher.doFinal(Base64.getDecoder().decode(encryptedPassword)))
    }

}

@Serializable
data class Login(var id:Int,var username: String, var password: String,var decryptedPassword: String)

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
                set(LoginTable.password,AESSecurity.encryptPassword(login.password))
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
                password = it[LoginTable.password]!!,
                decryptedPassword = AESSecurity.decryptPassword(it[LoginTable.password]!!))
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
