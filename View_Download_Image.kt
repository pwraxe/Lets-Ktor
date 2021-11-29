/**
Initially I place image in 'file'  folder in project and access same image
*/

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

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


fun Routing.dataRouting(){

    get("/viewImage"){

        val imageFile = File("files/img_1.png")
        call.response.header(HttpHeaders.ContentDisposition,
            ContentDisposition
            .Inline     //-----------------------------------------------------------------------> Inline : Image show in Browser
            .withParameter(ContentDisposition.Parameters.FileName,"img_1.png").toString())
        call.respondFile(imageFile)
    }

    get("/downloadImage"){
        val imageFile = File("files/img_1.png")
        call.response.header(HttpHeaders.ContentDisposition,
            ContentDisposition
            .Attachment //-----------------------------------------------------------------------> Attachment : Download Image
            .withParameter(ContentDisposition.Parameters.FileName,"img_1.png").toString())
        call.respondFile(imageFile)
    }
}
