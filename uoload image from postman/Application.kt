package com.codexdroid

import com.google.gson.Gson
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.http.content.*
import kotlinx.serialization.Serializable
import java.io.File

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)


@Serializable
data class Person(var id: Int, var name: String, var email: String)

//This is Path where you want to save image
private var FILE_PATH = "C:\\Users\\[username]\\OneDrive\\Desktop\\img"

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {}
    }

    install(StatusPages){
        exception<Throwable>{ cause ->
            call.respond(HttpStatusCode.InternalServerError,"Exception : $cause")
        }
    }
    routing {
        dataRoutes()
    }
}

fun Routing.dataRoutes(){

    get("/api"){
        call.respond(
            mapOf("data" to arrayOf(
                Person(1,"Akshay","akshay@gmail.com"),
                Person(2,"Alex","alex@gmail.com")
            ))
        )
    }

    get("/{id}"){
        when(call.parameters["id"]?.toInt() ?: call.respond("Invalid id ,refer id 1 or 2")){
            1 ->{ call.respond(Person(1,"Akshay","akshay@gmail.com"))}
            2 ->{call.respond(Person(2,"Alex","alex@gmail.com"),)}
            else -> { call.respond("Invalid id ,refer id 1 or 2") }
        }
    }
    /**
     *  -----------------------------------------------------------------------------------------------------------------
     *  Method : POST
     *  URL : http://localhost:8080/upload_img
     *  URL : http://localhost:8080/img_upload
     *
     *  POSTMAN
     *      Headers
     *          Accept : image/png
     *      Body
     *          form-data
     *              key:image
     *              value: file
     *
     *  -----------------------------------------------------------------------------------------------------------------
     *
     */

    var fileDescription = ""
    var fileName = ""

    // UPLOAD IMAGE METHOD 1
    post("/upload_img"){

        val multipartData = call.receiveMultipart()

        multipartData.forEachPart { part ->
            println("part.name =========> ${part.name}")
            println("part.contentType =========> ${part.contentType}")
            println("part.contentDisposition =========> ${part.contentDisposition}")
            println("part.contentDisposition?.name =========> ${part.contentDisposition?.name}")
            println("part.contentDisposition?.disposition =========> ${part.contentDisposition?.disposition}")
            println("Gson().toJson(part.contentDisposition?.parameters) =========> ${Gson().toJson(part.contentDisposition?.parameters)}")
            println("part.headers =========> ${part.headers}")
            println("part.headers.caseInsensitiveName =========> ${part.headers.caseInsensitiveName}")
            println("part.headers.names() =========> ${part.headers.names()}")
            println("part.headers.parseVersions() =========> ${part.headers.parseVersions()}")

            when (part) {
                is PartData.FormItem -> {
                    fileDescription = part.value
                }
                is PartData.FileItem -> {
                    fileName = part.originalFileName as String
                    val fileBytes = part.streamProvider().readBytes()
                    println("fileBytes : $fileBytes")
                    File("$FILE_PATH//$fileName").writeBytes(fileBytes)
                }
                else -> {}
            }
        }

        call.respondText("$fileDescription is uploaded to 'uploads/$fileName'")
    }

    // UPLOAD IMAGE METHOD 2
    post("/img_upload"){
        try {

            val multipart = call.receiveMultipart()

            multipart.forEachPart { part ->

                if(part is PartData.FileItem){

                    fileName = part.originalFileName!!

                    val file : File = if (fileName.endsWith(".jpg") ||
                        fileName.endsWith(".png") ||
                        fileName.endsWith(".jpeg") ||
                        fileName.endsWith(".gif")) {
                        println("if----> $FILE_PATH\\$fileName")
                        File("$FILE_PATH\\$fileName")

                    }else{
                        val ext = File(part.originalFileName!!).extension
                        println("else ----> $FILE_PATH\\${part.originalFileName}.$ext")
                        File("$FILE_PATH\\${part.originalFileName}.$ext")
                    }

                    part.streamProvider().use { input ->
                        file.outputStream().buffered().use { buffer ->
                            input.copyTo(buffer)
                        }
                    }
                }
                part.dispose()
                call.respond("File Successfully Uploaded")
            }

        }catch (ex : Exception){
            call.respond(ex)
        }
    }

    //View Image on Web-Browser   (Check path for this)
    get("/img/{name}"){
        val name: String = call.parameters["name"]!!
        println("Image Name : $name")

        if(name.endsWith(".jpg") ||
            name.endsWith("jpeg") ||
            name.endsWith("png") ||
            name.endsWith("gif")){

            val file = File("$FILE_PATH\\$name")
            println("FilePath : $file")
            if(file.exists()){
                call.respondFile(file)
            }else{
                call.respond("No such image")
            }
        }else{
            val file = File(FILE_PATH)
            val fileLists = file.list()

            println(Gson().toJson(fileLists))

            loop@for(imgFile in fileLists){
                if(name == imgFile){
                    call.respondFile(file)
                    break@loop
                }
            }
            call.respond("Image $name Not Found")
        }
    }
}
