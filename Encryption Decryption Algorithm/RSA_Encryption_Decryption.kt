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
import java.lang.Exception
import java.nio.charset.Charset
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import javax.crypto.Cipher

object RSASecurity{

    @Volatile private var keyPairGenerator: KeyPairGenerator? = null
    @Volatile private var cipher: Cipher? = null
    @Volatile private var keyPair : KeyPair? = null
    @Volatile private var privateKey: PrivateKey? = null
    @Volatile private var publicKey: PublicKey? = null

    init {
        try {

            synchronized(this){
                if(keyPairGenerator == null) keyPairGenerator = KeyPairGenerator.getInstance("RSA")
                if(cipher == null) cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
                if(keyPair == null) keyPair = keyPairGenerator?.generateKeyPair()
                keyPairGenerator?.initialize(1024)
                if(privateKey == null) privateKey = keyPair?.private
                if(publicKey == null) publicKey = keyPair?.public
            }

        }catch (ex : Exception){
            ex.printStackTrace()
        }
    }
    fun encryptPassword(password: String,publicKey: PublicKey) : String {
        cipher?.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher?.doFinal(password.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decryptPassword(password: String, privateKey: PrivateKey) : String {
        cipher?.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedMessage = cipher?.doFinal(Base64.getDecoder().decode(password))     //Issue causing here
        return String(decryptedMessage!!, Charset.forName("UTF8"))
    }

    fun getPrivateKey(): PrivateKey? = privateKey
    fun getPublicKey(): PublicKey? = publicKey
}

@Serializable
data class Login(var id:Int,var username: String, var password: String)

@Serializable
data class LoginResponse(var id: Int, var username: String, var password: String)

@Serializable
data class CommonResponse(var statusCode: Int, var message:String, val data:MutableList<LoginResponse>?)

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

    val loginList = arrayListOf<LoginResponse>()

    post("/insert"){
        val login = call.receive<Login>()
        val encryptedPassword = RSASecurity.encryptPassword(login.password, RSASecurity.getPublicKey()!!)

        val affectedRows = getDatabase()?.insert(LoginTable){
            set(LoginTable.id,login.id)
            set(LoginTable.username,login.username)
            set(LoginTable.password,encryptedPassword)
        }

        if(affectedRows == 1){
            call.respond(CommonResponse(statusCode = HttpStatusCode.OK.value, "Data inserted", null))
        }else{
            call.respond(CommonResponse(statusCode = HttpStatusCode.BadRequest.value, "Error to insert Data", null))
        }
    }

    get("/view"){
        //Following code like > SELECT * FROM PersonTable;
        loginList.clear()
        val query: Query? = getDatabase()?.from(LoginTable)?.select()
        for (row in query!!){

            loginList.add(
                LoginResponse(
                    row[LoginTable.id]!!,
                    row[LoginTable.username]!!,
                    RSASecurity.decryptPassword(row[LoginTable.password]!!, RSASecurity.getPrivateKey()!!)      //row[LoginTable.password]!!
                )
            )
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
