package com.hydrofish.app.api
data class RegisterError(
    val username: List<String>?,
    val password1: List<String>?,
    val password2: List<String>?
)