package com.hydrofish.app.api

data class PostSuccess(
    val status: String,
    val message: String,
    val level: Int = -1,
)
