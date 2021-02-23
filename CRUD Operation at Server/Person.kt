package com.codexdroid

import kotlinx.serialization.Serializable

@Serializable
data class Person(
    var id : Int,
    var name : String,
    var email : String,
    var mobile : Long
)