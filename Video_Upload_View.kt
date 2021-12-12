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



//Video URL : https://gist.github.com/jsturgis/3b19447b304616f18657  ========> git url where you can get video url for testing



//This is Path where you want to save videos
private var FILE_PATH = "C:\\Users\\[username]\\OneDrive\\Desktop\\videos"

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

    // UPLOAD VIDEO METHOD 1
    post("/upload_vid"){

        val multipartData = call.receiveMultipart()

        multipartData.forEachPart { part ->

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

        call.respondText("uploaded to '$FILE_PATH/$fileName'")
    }

    // UPLOAD VIDEO METHOD 2
    post("/vid_upload"){
        try {

            val multipart = call.receiveMultipart()

            multipart.forEachPart { part ->

                if(part is PartData.FileItem){

                    fileName = part.originalFileName!!

                    // .mp2 : MPEG-2
                    // .mp4 : MPEG-4 Part 14
                    // .mov : QuickTime multimedia file format
                    // .wmv : Windows Media Viewer
                    // .avi : Audio Video Interleave
                    // .avchd : Advanced Video Coding High Definition
                    // .flv : Flash video formats
                    // .swf : Shockwave Flash
                    // .mkv : Matroska Multimedia Container
                    // .webm, .html5


                    val file : File = if (
                        fileName.endsWith(".mp2") ||
                        fileName.endsWith(".mp4") ||
                        fileName.endsWith(".mov")||
                        fileName.endsWith(".wmv") ||
                        fileName.endsWith(".avi") ||
                        fileName.endsWith(".avchd") ||
                        fileName.endsWith(".flv") ||
                        fileName.endsWith(".swf") ||
                        fileName.endsWith(".mkv") ||
                        fileName.endsWith(".webm") ||
                        fileName.endsWith(".html5")) {
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

    get("/video/{video_name}"){
        fileName = call.parameters["video_name"]!!
        println("Video Name : $fileName")

        if(fileName.endsWith(".mp2") ||
            fileName.endsWith(".mp4") ||
            fileName.endsWith(".mov")||
            fileName.endsWith(".wmv") ||
            fileName.endsWith(".avi") ||
            fileName.endsWith(".avchd") ||
            fileName.endsWith(".flv") ||
            fileName.endsWith(".swf") ||
            fileName.endsWith(".mkv") ||
            fileName.endsWith(".webm") ||
            fileName.endsWith(".html5")){

            val file = File("$FILE_PATH\\$fileName")
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
                if(fileName == imgFile){
                    call.respondFile(file)
                    break@loop
                }
            }
            call.respond("Image $fileName Not Found")
        }
    }


}
