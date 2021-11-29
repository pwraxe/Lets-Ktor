
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

    route("/data"){
        get {
            println("call.request.cookies.rawCookies : ${call.request.cookies.rawCookies}")
            println("call.request.cookies.rawCookies.entries : ${call.request.cookies.rawCookies.entries}")

            println("call.request.headers.names() : ${call.request.headers.names()}")
            println("call.request.headers.caseInsensitiveName : ${call.request.headers.caseInsensitiveName}")
            println("call.request.headers.entries() : ${call.request.headers.entries()}")
            println("call.request.headers.parseVersions()  :  ${call.request.headers.parseVersions()}")

            println("call.request.local.host :  ${call.request.local.host}")
            println("call.request.local.method :  ${call.request.local.method}")
            println("call.request.local.port :  ${call.request.local.port}")
            println("call.request.local.uri :  ${call.request.local.uri}")
            println("call.request.local.remoteHost :  ${call.request.local.remoteHost}")
            println("call.request.local.scheme :  ${call.request.local.scheme}")
            println("call.request.local.version :  ${call.request.local.version}")

            println("call.request.pipeline.attributes.allKeys  :  ${call.request.pipeline.attributes.allKeys}")
            println("call.request.pipeline.items  :  ${call.request.pipeline.items}")
            println("call.request.pipeline.developmentMode  :  ${call.request.pipeline.developmentMode}")

            println("call.request.queryParameters.entries()  : ${call.request.queryParameters.entries()}")
            println("call.request.queryParameters.urlEncodingOption.name  : ${call.request.queryParameters.urlEncodingOption.name}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.name  : ${call.request.queryParameters.urlEncodingOption.declaringClass.name}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.declaringClass  : ${call.request.queryParameters.urlEncodingOption.declaringClass.declaringClass}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.classes  : ${call.request.queryParameters.urlEncodingOption.declaringClass.classes}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.declaredClasses  : ${call.request.queryParameters.urlEncodingOption.declaringClass.declaredClasses}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.classLoader  : ${call.request.queryParameters.urlEncodingOption.declaringClass.classLoader}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.annotatedInterfaces  : ${call.request.queryParameters.urlEncodingOption.declaringClass.annotatedInterfaces}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.canonicalName  : ${call.request.queryParameters.urlEncodingOption.declaringClass.canonicalName}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.constructors  : ${call.request.queryParameters.urlEncodingOption.declaringClass.constructors}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.enclosingClass  : ${call.request.queryParameters.urlEncodingOption.declaringClass.enclosingClass}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.enclosingConstructor  : ${call.request.queryParameters.urlEncodingOption.declaringClass.enclosingConstructor}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.enclosingMethod  : ${call.request.queryParameters.urlEncodingOption.declaringClass.enclosingMethod}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.enumConstants  : ${call.request.queryParameters.urlEncodingOption.declaringClass.enumConstants}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.fields  : ${call.request.queryParameters.urlEncodingOption.declaringClass.fields}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.module  : ${call.request.queryParameters.urlEncodingOption.declaringClass.module}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.nestHost  : ${call.request.queryParameters.urlEncodingOption.declaringClass.nestHost}")
            println("call.request.queryParameters.urlEncodingOption.declaringClass.packageName  : ${call.request.queryParameters.urlEncodingOption.declaringClass.packageName}")
            println("call.request.queryParameters.urlEncodingOption.name  : ${call.request.queryParameters.urlEncodingOption.name}")
            println("call.request.queryParameters.urlEncodingOption.ordinal  : ${call.request.queryParameters.urlEncodingOption.ordinal}")
            println("call.request.queryParameters.names()  : ${call.request.queryParameters.names()}")
            println("call.request.queryParameters.formUrlEncode()  : ${call.request.queryParameters.formUrlEncode()}")


            println("call.request.receiveChannel().availableForRead :  ${call.request.receiveChannel().availableForRead}")
            println("call.request.receiveChannel().closedCause?.message :  ${call.request.receiveChannel().closedCause?.message}")
            println("call.request.receiveChannel().closedCause?.localizedMessage :  ${call.request.receiveChannel().closedCause?.localizedMessage}")
            println("call.request.receiveChannel().isClosedForRead :  ${call.request.receiveChannel().isClosedForRead}")
            println("call.request.receiveChannel().totalBytesRead :  ${call.request.receiveChannel().totalBytesRead}")
            println("call.request.receiveChannel().isClosedForWrite :  ${call.request.receiveChannel().isClosedForWrite}")
            //println("call.request.receiveChannel().readBoolean() :  ${call.request.receiveChannel().readBoolean()}")
            //println("call.request.receiveChannel().readByte() :  ${call.request.receiveChannel().readByte()}")
            //println("call.request.receiveChannel().readDouble() :  ${call.request.receiveChannel().readDouble()}")
            //println("call.request.receiveChannel().readInt() :  ${call.request.receiveChannel().readInt()}")
            //println("call.request.receiveChannel().readFloat() :  ${call.request.receiveChannel().readFloat()}")
            //println("call.request.receiveChannel().readLong() :  ${call.request.receiveChannel().readLong()}")
            //println("call.request.receiveChannel().readShort() :  ${call.request.receiveChannel().readShort()}")
            println("call.request.receiveChannel().totalBytesRead :  ${call.request.receiveChannel().totalBytesRead}")

            println(" call.request.origin.scheme  : ${call.request.origin.scheme}")
            println(" call.request.origin.uri  : ${call.request.origin.uri}")
            println(" call.request.origin.port  : ${call.request.origin.port}")
            println(" call.request.origin.version  : ${call.request.origin.version}")
            println(" call.request.origin.method.value  : ${call.request.origin.method.value}")
            println(" call.request.origin.host  : ${call.request.origin.host}")
            println(" call.request.origin.remoteHost  : ${call.request.origin.remoteHost}")
            println(" call.request.toLogString()  : ${call.request.toLogString()}")
            println(" call.request.httpMethod.value  : ${call.request.httpMethod.value}")
            println(" call.request.httpVersion  : ${call.request.httpVersion}")
            println(" call.request.uri  : ${call.request.uri}")
            println(" call.request.accept()  : ${call.request.accept()}")
            println(" call.request.acceptCharset()  : ${call.request.acceptCharset()}")
            println(" call.request.acceptCharsetItems()  : ${call.request.acceptCharsetItems()}")
            println(" call.request.acceptItems()  : ${call.request.acceptItems()}")
            println(" call.request.acceptEncoding()  : ${call.request.acceptEncoding()}")
            println(" call.request.acceptLanguage()  : ${call.request.acceptLanguage()}")
            println(" call.request.acceptLanguageItems()  : ${call.request.acceptLanguageItems()}")
            println(" call.request.authorization()  : ${call.request.authorization()}")
            println(" call.request.cacheControl()  : ${call.request.cacheControl()}")
            println(" call.request.contentCharset()  : ${call.request.contentCharset()}")
            println(" call.request.contentType()  : ${call.request.contentType()}")
            println(" call.request.document()  : ${call.request.document()}")
            println(" call.request.host()  : ${call.request.host()}")
            println(" call.request.isChunked()  : ${call.request.isChunked()}")
            println(" call.request.isMultipart()  : ${call.request.isMultipart()}")
            println(" call.request.location()  : ${call.request.location()}")
            println(" call.request.path()  : ${call.request.path()}")
            println(" call.request.port()  : ${call.request.port()}")
            println(" call.request.queryString()  : ${call.request.queryString()}")
            println(" call.request.ranges()  : ${call.request.ranges()}")
            println(" call.request.document()  : ${call.request.document()}")
            println(" call.request.userAgent()  : ${call.request.userAgent()}")
            println(" call.request.parseAuthorizationHeader()?.authScheme  : ${call.request.parseAuthorizationHeader()?.authScheme}")
            println(" call.request.parseAuthorizationHeader()?.render()  : ${call.request.parseAuthorizationHeader()?.render()}")
        }
    }
}
------------------------------------------------------------ OUTPUT ------------------------------------------------------------------------
 
call.request.cookies.rawCookies : {}
call.request.cookies.rawCookies.entries : []
call.request.headers.names() : ["Host","Connection","sec-ch-ua","Cache-Control","sec-ch-ua-mobile","User-Agent","sec-ch-ua-platform","Postman-Token","Accept","Sec-Fetch-Site","Sec-Fetch-Mode","Sec-Fetch-Dest","Accept-Encoding","Accept-Language"]
call.request.headers.caseInsensitiveName : true
call.request.headers.entries() : [null,null,null,null,null,null,null,null,null,null,null,null,null,null]
call.request.headers.parseVersions()  :  []
call.request.local.host :  localhost
call.request.local.method :  HttpMethod(value=GET)
call.request.local.port :  8080
call.request.local.uri :  /data
call.request.local.remoteHost :  0:0:0:0:0:0:0:1
call.request.local.scheme :  http
call.request.local.version :  HTTP/1.1
call.request.pipeline.attributes.allKeys  :  []
call.request.pipeline.items  :  [Phase('Before'), Phase('Transform'), Phase('After')]
call.request.pipeline.developmentMode  :  false
call.request.queryParameters.entries()  : []  <--------------------------------------------------------------------When client send payload we can grab it here at server side
call.request.queryParameters.urlEncodingOption.name  : DEFAULT
call.request.queryParameters.urlEncodingOption.declaringClass.name  : "io.ktor.http.UrlEncodingOption"
call.request.queryParameters.urlEncodingOption.declaringClass.declaringClass  : null
call.request.queryParameters.urlEncodingOption.declaringClass.classes  : [Ljava.lang.Class;@25d41ec4
call.request.queryParameters.urlEncodingOption.declaringClass.declaredClasses  : []
call.request.queryParameters.urlEncodingOption.declaringClass.classLoader  : jdk.internal.loader.ClassLoaders$AppClassLoader@1d44bcfa
call.request.queryParameters.urlEncodingOption.declaringClass.annotatedInterfaces  : []
call.request.queryParameters.urlEncodingOption.declaringClass.canonicalName  : "io.ktor.http.UrlEncodingOption"
call.request.queryParameters.urlEncodingOption.declaringClass.constructors  : []
call.request.queryParameters.urlEncodingOption.declaringClass.enclosingClass  : null
call.request.queryParameters.urlEncodingOption.declaringClass.enclosingConstructor  : null
call.request.queryParameters.urlEncodingOption.declaringClass.enclosingMethod  : null
call.request.queryParameters.urlEncodingOption.declaringClass.enumConstants  : ["DEFAULT","KEY_ONLY","VALUE_ONLY","NO_ENCODING"]
call.request.queryParameters.urlEncodingOption.declaringClass.fields  : [{},{},{},{}]
call.request.queryParameters.urlEncodingOption.declaringClass.module  : {}
call.request.queryParameters.urlEncodingOption.declaringClass.nestHost  : class io.ktor.http.UrlEncodingOption
call.request.queryParameters.urlEncodingOption.declaringClass.packageName  : "io.ktor.http"
call.request.queryParameters.urlEncodingOption.name  : DEFAULT
call.request.queryParameters.urlEncodingOption.ordinal  : 0
call.request.queryParameters.names()  : []
call.request.queryParameters.formUrlEncode()  : 
call.request.receiveChannel().availableForRead :  0
call.request.receiveChannel().closedCause?.message :  null
call.request.receiveChannel().closedCause?.localizedMessage :  null
call.request.receiveChannel().isClosedForRead :  true
call.request.receiveChannel().totalBytesRead :  0
call.request.receiveChannel().isClosedForWrite :  true
call.request.receiveChannel().totalBytesRead :  0
 call.request.origin.scheme  : http
 call.request.origin.uri  : /data
 call.request.origin.port  : 8080
 call.request.origin.version  : HTTP/1.1
 call.request.origin.method.value  : GET
 call.request.origin.host  : localhost
 call.request.origin.remoteHost  : 0:0:0:0:0:0:0:1
 call.request.toLogString()  : GET - /data
 call.request.httpMethod.value  : GET
 call.request.httpVersion  : HTTP/1.1
 call.request.uri  : /data
 call.request.accept()  : */*
 call.request.acceptCharset()  : null
 call.request.acceptCharsetItems()  : []
 call.request.acceptItems()  : [HeaderValue(value=*/*, params=[])]
 call.request.acceptEncoding()  : gzip, deflate, br
 call.request.acceptLanguage()  : en-US,en;q=0.9,da;q=0.8,hi;q=0.7
 call.request.acceptLanguageItems()  : [HeaderValue(value=en-US, params=[]), HeaderValue(value=en, params=[HeaderValueParam(name=q, value=0.9)]), HeaderValue(value=da, params=[HeaderValueParam(name=q, value=0.8)]), HeaderValue(value=hi, params=[HeaderValueParam(name=q, value=0.7)])]
 call.request.authorization()  : null
 call.request.cacheControl()  : no-cache
 call.request.contentCharset()  : null
 call.request.contentType()  : */*
 call.request.document()  : data
 call.request.host()  : localhost
 call.request.isChunked()  : false
 call.request.isMultipart()  : false
 call.request.location()  : null
 call.request.path()  : /data
 call.request.port()  : 8080
 call.request.queryString()  : 
 call.request.ranges()  : null
 call.request.document()  : data
 call.request.userAgent()  : Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36
 call.request.parseAuthorizationHeader()?.authScheme  : null
 call.request.parseAuthorizationHeader()?.render()  : null
