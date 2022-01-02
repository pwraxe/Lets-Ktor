buildscript{ ...
}
plugins {
    id "org.jetbrains.kotlin.plugin.serialization" version "1.6.0"
}


task fatJar(type: Jar) {
    manifest {
        attributes 'Main-Class': mainClassName
    }
    from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
    with jar
}

dependencies{
  ...
    implementation "io.ktor:ktor-server-core:$ktor_version"
    implementation "io.ktor:ktor-gson:$ktor_version"
    implementation "io.ktor:ktor-client-core:$ktor_version"
    implementation "io.ktor:ktor-client-core-jvm:$ktor_version"
    implementation "io.ktor:ktor-client-json-jvm:$ktor_version"
    implementation "io.ktor:ktor-client-gson:$ktor_version"
  
    implementation "io.ktor:ktor-serialization:$ktor_version"
    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2'
    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2'

    //mysql
    implementation group: 'mysql', name: 'mysql-connector-java', version: '8.0.27'                                      //MySQL
    //ktorm
    implementation group: 'org.ktorm', name: 'ktorm-core', version: '3.4.1'

    //Authentication Token
    implementation "io.ktor:ktor-auth:$ktor_version" //1.6.6
    implementation "io.ktor:ktor-auth-jwt:$ktor_version"  //1.6.6
}

-------------------------------------------------------------------- gradle.properties --------------------------------------------------------------
ktor_version=1.6.7
kotlin.code.style=official
kotlin_version=1.6.10
logback_version=1.2.10

-------------------------------------------------------------------- application.conf ---------------------------------------------------------------
  
secret = "key_@100796_akshay"                   # any secrete Key
issuer = "http://localhost:8086/"               # or can be > http://0.0.0.0:88080/
audience = "http://localhost:8086/welcome"      # set to default URL
realm = "Access to 'welcome'"

-------------------------------------------------------------------- TokenManager.kt ----------------------------------------------------------------
  
  
class TokenManager(val config : HoconApplicationConfig) {

    private val oneMin = 60_000
    private val oneHr = oneMin * 60

    private val audience = config.property("audience").getString()
    private val secret = config.property("secret").getString()
    private val issuer = config.property("issuer").getString()
    private val tokenExpiration = Date(System.currentTimeMillis().plus(5000L))   //Token expired after every 5 sec


    //We should be compulsory pass here to generate new token base on unit key
    //here I am passing different mobile no to generate different token

    fun generateToken(mobile:String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("mobile",mobile)
            .withExpiresAt(tokenExpiration)
            .sign(Algorithm.HMAC256(secret))
    }

    /**
    fun verifyToken() : JWTVerifier{
        return JWT.require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }
    */
}

-------------------------------------------------------------------- Application.kt ---------------------------------------------------------------

package com.codexdroid

import com.google.gson.Gson
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.routing.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.Serializable
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

private var database: Database? = null
val config = HoconApplicationConfig(ConfigFactory.load())
private var tokenManager = TokenManager(config)
val personList = mutableListOf<PersonRespond>()

object PersonTable : Table<Nothing>(tableName = "person") {
    val id = int("id").primaryKey()
    val name = varchar("name")
    val email = varchar("email")
    val mobile = varchar("mobile")
    val token = varchar("token")
}

//Received Class
@Serializable
data class PersonRequest(var id: Int,var name: String, var email: String, var mobile: String)

//Respond Class
@Serializable
data class PersonRespond(var id: Int, var name: String, var email: String, var mobile: String, var token: String)

@Serializable
data class CommonResponse(var status: Int, var message: String, var data: List<PersonRespond>?)

@Serializable
data class OneRowResponse(var status: Int, var message: String, var data: PersonRespond?)

@Serializable
data class Tokens(var token :String?)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            setLenient()
        }
    }

    /****
    install(Authentication) {
        jwt {
            verifier(tokenManager.verifyToken())
            realm = config.property("realm").getString()
            validate { jwtCredential ->

                val email = jwtCredential.payload.getClaim("email").asString()
                if(email.isNotEmpty() || email.endsWith("@gmail.com")){
                    JWTPrincipal(jwtCredential.payload)
                }else null

            }
        }
    }
    ***/

    routing {

        get("/welcome") {
            call.respond("Welcome to codexdroid.com")
        }
        registerUser()
        loginUser()
        displayUsers()
        displaySingleUser()
        updateUser()
        deleteUser()
    }
}

private fun getDatabase(): Database? {
    database = if (database == null) {
        Database.connect(
            url = "jdbc:mysql://localhost/test_db",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "root",
            password = ""

        )
    } else database
    return database
}

private fun checkUserExistsOrNot(request: PersonRequest) : PersonRespond? {
    return getDatabase()?.from(PersonTable)
        ?.select()
        ?.where{ PersonTable.mobile eq request.mobile }
        ?.map { PersonRespond(
            it[PersonTable.id]!!,
            it[PersonTable.name]!!,
            it[PersonTable.email]!!,
            it[PersonTable.mobile]!!,
            it[PersonTable.token]!!,
        )
        }?.firstOrNull()
}

private fun getServerToken(clientToken : String) : Tokens?{

    return getDatabase()
        ?.from(PersonTable)
        ?.select(PersonTable.token)
        ?.where { PersonTable.token eq clientToken }
        ?.map { queryRowSet -> Tokens(queryRowSet[PersonTable.token]) }
        ?.firstOrNull()
}

private fun Routing.registerUser() {

    post("/register") {

        val request: PersonRequest = call.receive()

        if(checkUserExistsOrNot(request) != null){
            //User Already Exists
            call.respond(CommonResponse(HttpStatusCode.Found.value,"Mobile No ${request.mobile} already Exists, Please Login",null))
        }else{
            //User Not registered with Us
            val token = "Bearer ${tokenManager.generateToken(request.mobile)}"
            val affectedRows = getDatabase()?.insert(PersonTable) {
                set(PersonTable.id, 0)
                set(PersonTable.name, request.name)
                set(PersonTable.email, request.email)
                set(PersonTable.mobile, request.mobile)
                set(PersonTable.token, token)
            }
            personList.clear()
            personList.add(PersonRespond(request.id,request.name,request.email,request.mobile,token))

            if (affectedRows!! > 0) {
                call.respond(CommonResponse(HttpStatusCode.OK.value, "Data Inserted Successfully", personList))
            } else {
                call.respond(CommonResponse(HttpStatusCode.NotFound.value, "Error to Insert Data", null))
            }
        }
    }
}

private fun Routing.loginUser(){
    post("/login"){
        val request = call.receive<PersonRequest>()
        val person = checkUserExistsOrNot(request)
        if( person != null){
            if(request.mobile == person.mobile){
                val affectedRows = getDatabase()?.update(PersonTable){
                    val token = tokenManager.generateToken(request.mobile)
                    set(PersonTable.token,token)
                }
                if(affectedRows!! > 0){
                    call.respond(CommonResponse(HttpStatusCode.Found.value,"User Login Successfully",null))
                }else{
                    call.respond(CommonResponse(HttpStatusCode.NotFound.value,"Invalid Mobile entered",null))
                }
            }
        }
    }
}

private fun Routing.displayUsers() {

    //Logic : Existing User Can See List of other user based on Token

    get("/view") {
        val clientToken = call.request.authorization().toString()
        if (clientToken == getServerToken(clientToken)?.token) {
            personList.clear()
            val query = getDatabase()?.from(PersonTable)?.select()
            for (row in query!!) {
                personList.add(
                    PersonRespond(
                        row[PersonTable.id]!!,
                        row[PersonTable.name]!!,
                        row[PersonTable.email]!!,
                        row[PersonTable.mobile]!!,
                        row[PersonTable.token]!!,
                    )
                )
            }
            if (personList.isNotEmpty()) {
                call.respond(CommonResponse(HttpStatusCode.OK.value, "All Data Retrieved Successfully", personList))
            } else {
                call.respond(CommonResponse(HttpStatusCode.OK.value, "Some Error detected to Retrieved Data", null))
            }
        }else {
            call.respond(CommonResponse(HttpStatusCode.NotFound.value,"Please Provide Token to get access",null))
        }
    }
}

private fun Routing.displaySingleUser(){

    get("/view/{id}"){

        val clientToken = call.request.authorization().toString()
        if (clientToken == getServerToken(clientToken)?.token) {
            val id = call.parameters["id"]?.toInt()
            val person = getDatabase()?.from(PersonTable)?.select()?.where{ PersonTable.id eq id!!
            }?.map {
                PersonRespond(
                    it[PersonTable.id]!!,
                    it[PersonTable.name]!!,
                    it[PersonTable.email]!!,
                    it[PersonTable.mobile]!!,
                    it[PersonTable.token]!!,
                )
            }?.firstOrNull()

            if(person != null){
                call.respond(OneRowResponse(HttpStatusCode.Found.value,"We Found Your Data", person))
            }else{
                call.respond(OneRowResponse(HttpStatusCode.NotFound.value,"We Cannot Found Your Data", null))
            }
        }else{
            call.respond(CommonResponse(HttpStatusCode.NotFound.value,"Please Provide Token to get access",null))
        }
    }
}

private fun Routing.updateUser(){

    put("/update"){

        val clientToken = call.request.authorization().toString()
        if (clientToken == getServerToken(clientToken)?.token) {

            val person = call.receive<PersonRequest>()
            val affectedRow = getDatabase()?.update(PersonTable){
                set(PersonTable.name,person.name)
                set(PersonTable.email,person.email)
                set(PersonTable.mobile,person.mobile)
                where { (PersonTable.id eq person.id) and (PersonTable.mobile eq person.mobile)}
            }
            if(affectedRow!! > 0){
                call.respond(CommonResponse(status = HttpStatusCode.OK.value, message = "Data of ${person.name} Updated",null))
            }else {
                call.respond(CommonResponse(status = HttpStatusCode.NotFound.value, message = "Unable to Update data,id, mobile and token should be same", null))
            }
        }else{
            call.respond(CommonResponse(HttpStatusCode.NotFound.value,"Please Provide Token to get access",null))
        }
    }
}

private fun Routing.deleteUser(){

    //Make sure Id and token is in same row to delete data

    delete("/deleteUser/{id}"){

        val clientToken = call.request.authorization().toString()

        if (clientToken == getServerToken(clientToken)?.token) {

            val id = call.parameters["id"]?.toInt()

            val person = getDatabase()?.from(PersonTable)?.select()?.where{ PersonTable.id eq id!! }?.map {
                PersonRespond(
                    it[PersonTable.id]!!,
                    it[PersonTable.name]!!,
                    it[PersonTable.email]!!,
                    it[PersonTable.mobile]!!,
                    it[PersonTable.token]!!)
            }?.firstOrNull()

            println("Person : ${Gson().toJson(person)} ====> $id == ${person?.id}")

            if(person?.id == id){
                val affectedRow = getDatabase()?.delete(PersonTable){ PersonTable.id eq id!! }

                if(affectedRow!! > 0){
                    call.respond(CommonResponse(status = HttpStatusCode.OK.value, message = "Data Deleted",null))
                }else {
                    call.respond(CommonResponse(status = HttpStatusCode.NotFound.value, message = "Unable to Delete Data, please send correct Id", null))
                }
            }else{
                call.respond(CommonResponse(status = HttpStatusCode.NotFound.value, message = "You are not authorised to delete data, please send correct id and token", null))
            }
        }else{
            call.respond(CommonResponse(HttpStatusCode.NotFound.value,"Please Provide Token to get access",null))
        }
    }
}






  
  
