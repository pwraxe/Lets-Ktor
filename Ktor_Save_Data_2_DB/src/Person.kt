package com.codexdroid

import kotlinx.serialization.Serializable
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.long
import org.ktorm.schema.varchar

@Serializable
data class Person(
    var id : Int,
    var name : String,
    var email : String,
    var mobile : Long
)

@Serializable
data class FailResponse(
    var status : Int,
    var message : String,
    var isSuccess : Boolean
)



@Serializable
data class SuccessResponse(
    var status: Int,
    var message: String,
    var isSuccess: Boolean
)


//When you use ktorm then describes your table structure
object Human : Table<Nothing>("human"){
    val id = int("id").primaryKey()
    val name = varchar("name")
    val email = varchar("email")
    var mobile = long("mobile")

}

