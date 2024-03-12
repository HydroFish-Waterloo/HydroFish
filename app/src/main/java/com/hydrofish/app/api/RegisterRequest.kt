package com.hydrofish.app.api

data class RegisterRequest (
    val username: String,
    val password1: String,
    val password2: String
)