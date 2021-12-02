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

    private lateinit var cipher: Cipher
    private lateinit var privateKey: PrivateKey
    private lateinit var publicKey: PublicKey

    init {
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(1024)

            val keyPair : KeyPair = keyPairGenerator.generateKeyPair()
            privateKey = keyPair.private
            publicKey = keyPair.public

        }catch (ex : Exception){
            ex.printStackTrace()
        }
    }
    fun encryptPassword(password: String) : String {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(password.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decryptPassword(password: String) : String {
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedMessage = cipher.doFinal(Base64.getDecoder().decode(password))
        return String(decryptedMessage, Charset.forName("UTF8"))
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

    post("/login"){
        val login = call.receive<Login>()
        val affectedRows = getDatabase()?.insert(LoginTable){
            set(it.id,0)
            set(it.username,login.username)
            set(it.password,RSASecurity.encryptPassword(login.password))
        }

        if(affectedRows == 1){
            call.respond(CommonResponse(statusCode = HttpStatusCode.OK.value, message = "Login Success",null))
        }else{
            call.respond(CommonResponse(statusCode = HttpStatusCode.NotFound.value, message = "Login Fail",null))
        }
    }

    get("/view"){
        loginList.clear()
        val query: Query? = getDatabase()?.from(LoginTable)?.select()
        for (row in query!!){
            loginList.add(Login(row[LoginTable.id]!!, row[LoginTable.username]!!, RSASecurity.decryptPassword(row[LoginTable.password]!!)))
        }
        if(loginList.isNotEmpty()){
            call.respond(CommonResponse(statusCode = HttpStatusCode.OK.value,"All Login Details",loginList))
        }else{
            call.respond(CommonResponse(statusCode = HttpStatusCode.NotFound.value,"No Data Found",null))
        }
    }
}

