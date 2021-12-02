
// ============================================ Insert Single Object in Database coming from client ========================================


@Serializable
data class Person(var id:Int,var name: String,var email:String,var mobile:Long)

@Serializable
data class CommonResponse(var statusCode: Int, var message:String, var data: List<Person>?)

//Create table for inserting data into database
object PersonTable : Table<Nothing>(tableName = "person"){
    val id = int("id").primaryKey() ||  val id = integer("id").primaryKey().autoIncrement()
    val name = varchar("name")
    val email = varchar("email")
    val mobile = long("mobile")
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

    val personsList = arrayListOf<Person>()

    post("/add"){
        val person = call.receive<Person>()
        val affectedRows = getDatabase()?.insert(PersonTable){
            set(it.name,person.name)
            set(it.email,person.email)
            set(it.mobile,person.mobile)
        }
        if(affectedRows == 1){
            call.respond(CommonResponse(statusCode = HttpStatusCode.OK.value, message = "Person ${person.name} Added in Database",null))
        }else{
            call.respond(CommonResponse(statusCode = HttpStatusCode.NotFound.value, message = "Error to Add data in Database",null))
        }
    }

    get("/view"){
        //Following code like > SELECT * FROM PersonTable;
        personsList.clear()
        val query: Query? = getDatabase()?.from(PersonTable)?.select()
        for (row in query!!){
            personsList.add(Person(row[PersonTable.id]!!,row[PersonTable.name]!!,row[PersonTable.email]!!,row[PersonTable.mobile]!!))
        }
        if(personsList.isNotEmpty()){
            call.respond(CommonResponse(statusCode = HttpStatusCode.OK.value, message = "${personsList.size} Person(s) Found",personsList))
        }else{
            call.respond(CommonResponse(statusCode = HttpStatusCode.NotFound.value, message = "No Person(s) Found",null))
        }
    }

    get("/view/{id}"){
        personsList.clear()
        val id : Int = call.parameters["id"]?.toInt() ?: return@get call.respond(CommonResponse(statusCode = HttpStatusCode.BadRequest.value,"Invalid parameter",null))
        val person: Person? = getDatabase()?.from(PersonTable)?.select()?.where { PersonTable.id eq id }?.map {
                Person(it[PersonTable.id]!!,
                    it[PersonTable.name]!!,
                    it[PersonTable.email]!!,
                    it[PersonTable.mobile]!!)
            }?.firstOrNull()
        person?.let {
            personsList.add(person)
        }

        if(person != null){
            call.respond(CommonResponse(HttpStatusCode.OK.value,"Person Found",personsList))
        }else{
            call.respond(CommonResponse(HttpStatusCode.BadRequest.value,"No Person Found of id = $id",null))
        }
    }

    post("/update"){
        val person = call.receive<Person>()
        val affectedRows = getDatabase()?.update(table = PersonTable){
            set(it.name,person.name)
            set(it.email,person.email)
            set(it.mobile,person.mobile)
            where {
                it.id eq person.id
            }
        }
        if(affectedRows == 1){  // 1 Row affected i.e. Data Updated  because we apply where clause to update single row
            call.respond(CommonResponse(statusCode = HttpStatusCode.OK.value, message = "${person.name} Updated",null))
        }else {
            call.respond(CommonResponse(statusCode = HttpStatusCode.NotFound.value, message = "Data Could not Updated", null))
        }
    }

    post("/delete"){
        val person = call.receive<Person>()
        val affectedRows = getDatabase()?.delete(table = PersonTable){
            it.id eq person.id
        }
        if(affectedRows == 1){
            call.respond(CommonResponse(statusCode = HttpStatusCode.OK.value, message = "${person.name} Deleted From Database",null))
        }else{
            call.respond(CommonResponse(statusCode = HttpStatusCode.NotFound.value, message = "Could not delete",null))
        }
    }

    post("/delete/{id}"){
        val id = call.parameters["id"].toString().toInt()
        val affectedRows = getDatabase()?.delete(table = PersonTable){
            it.id eq id
        }
        if(affectedRows == 1){
            call.respond(CommonResponse(statusCode = HttpStatusCode.OK.value, message = "Id $id deleted",null))
        }else{
            call.respond(CommonResponse(statusCode = HttpStatusCode.NotFound.value, message = "Id $id not available in database",null))
        }
    }
}
