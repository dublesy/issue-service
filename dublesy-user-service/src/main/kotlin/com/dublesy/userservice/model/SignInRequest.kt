package com.dublesy.userservice.model

data class SignInRequest(
    val email: String,
    val password: String,
)