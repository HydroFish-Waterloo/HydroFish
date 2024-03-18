package com.hydrofish.app.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type:application/json")
    @POST("/users/api/login/")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<AuthSuccess>

    @Headers("Content-Type:application/json")
    @POST("/users/api/register/")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<AuthSuccess>
}