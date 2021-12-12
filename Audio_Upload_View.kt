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

//This is Path where you want to save videos
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

    // UPLOAD VIDEO METHOD 1
    post("/upload_audio"){

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

        call.respondText("File uploaded to '$FILE_PATH/$fileName'")
    }

    // UPLOAD VIDEO METHOD 2
    post("/audio_upload"){
        try {

            val multipart = call.receiveMultipart()

            multipart.forEachPart { part ->

                if(part is PartData.FileItem){

                    fileName = part.originalFileName!!

                            /***
                             * See URL : https://fileinfo.com/filetypes/
                             *
                             .flac : Free Lossless Audio Codec.
                             .m4a  : mpeg-4 audio
                             .mp3  : MPEG audio layer 3
                             .wav  : Waveform Audio File
                             .wma  : Windows Media Audio
                             .aac  : AAC (Advanced Audio Coding)
                             .ogg  : Ogg Vorbis Audio File
                             .aif  : Audio Interchange File Format
                             .ec3  :Enhanced Audio Codec 3 File
                             .weba   : WebM Audio File
                             .sfk : Sound Forge Pro Audio Peak File
                             .mka  :Matroska Audio File
                             .omg  : OpenMG Audio File
                             .saf : Secure Audio File
                             .f32 : Raw 32-bit  Audio Format
                             .aac  : Advance audio coding file
                            **/

                    val file : File = if (
                        fileName.endsWith(".flac") ||
                        fileName.endsWith(".m4a") ||
                        fileName.endsWith(".mp3")||
                        fileName.endsWith(".wav") ||
                        fileName.endsWith(".wma") ||
                        fileName.endsWith(".aac") ||
                        fileName.endsWith(".ogg") ||
                        fileName.endsWith(".aif") ||
                        fileName.endsWith(".ec3") ||
                        fileName.endsWith(".weba") ||
                        fileName.endsWith(".sfk") ||
                        fileName.endsWith(".mka") ||
                        fileName.endsWith(".omg") ||
                        fileName.endsWith(".saf") ||
                        fileName.endsWith(".f32") ||
                        fileName.endsWith(".aac")) {
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

    get("/files/{audio_name}"){
        fileName = call.parameters["audio_name"]!!
        println("audio name : $fileName")

        if(fileName.endsWith(".flac")||
            fileName.endsWith(".m4a") ||
            fileName.endsWith(".mp3") ||
            fileName.endsWith(".wav") ||
            fileName.endsWith(".wma") ||
            fileName.endsWith(".aac") ||
            fileName.endsWith(".ogg") ||
            fileName.endsWith(".aif") ||
            fileName.endsWith(".ec3") ||
            fileName.endsWith(".weba")||
            fileName.endsWith(".sfk") ||
            fileName.endsWith(".mka") ||
            fileName.endsWith(".omg") ||
            fileName.endsWith(".saf") ||
            fileName.endsWith(".f32") ||
            fileName.endsWith(".aac")){

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
