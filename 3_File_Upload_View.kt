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

//This is Path where you want to save files
private var FILE_PATH = "C:\\Users\\AKSHAY\\OneDrive\\Desktop\\files"

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

    var fileDescription = ""
    var fileName = ""

    // UPLOAD FILE METHOD 1
    post("/upload_file"){

        val multipartData = call.receiveMultipart()
        multipartData.forEachPart { part ->

            when (part) {
                is PartData.FormItem -> {
                    fileDescription = part.value
                }
                is PartData.FileItem -> {
                    fileName = part.originalFileName as String
                    val fileBytes = part.streamProvider().readBytes()
                    File("$FILE_PATH//$fileName").writeBytes(fileBytes)
                }
                else -> {}
            }
        }

        call.respondText("File uploaded to '$FILE_PATH\\$fileName'")
    }

    // UPLOAD FILE METHOD 2
    post("/file_upload"){
        try {

            val multipart = call.receiveMultipart()

            multipart.forEachPart { part ->

                if(part is PartData.FileItem){

                    fileName = part.originalFileName!!

                    val file = File("$FILE_PATH\\$fileName")
                    val ext = File(fileName).extension
                    println("File : $file + $ext")
                    //File("$FILE_PATH\\${part.originalFileName}.$ext")

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

    //GET FILE TO BROWSER METHOD-1
    get("/old_files/{file_name}"){
        fileName = call.parameters["file_name"]!!
        println("audio name : $fileName")

        val file = File("$FILE_PATH\\$fileName")

        if(file.exists()){
            call.respondFile(file)
        }else{
            call.respond("No such file")
        }
    }

    //GET FILE TO BROWSER METHOD-2
    get("/new_files/{file_name}"){
        fileName = call.parameters["file_name"]!!
        println("audio name : $fileName")

        val file = File(FILE_PATH)
        val fileList = file.list()
        println("File List : $fileList")

        loop@for (f in fileList){
            if(fileName == f){
                call.respondFile(File("$FILE_PATH\\$f"))
                break@loop
            }
        }
        call.respond("No File found")
    }
}
