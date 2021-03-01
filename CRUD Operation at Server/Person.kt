package com.codexdroid

import kotlinx.serialization.Serializable

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
    var isSuccess: Boolean,
    var person: Person
)
