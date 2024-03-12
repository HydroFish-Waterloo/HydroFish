package com.hydrofish.app.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type:application/json")
    @POST("/users/api/login/")
    fun loginUser(@Body loginRequest: LoginRequest): Call<AuthSuccess>

    @Headers("Content-Type:application/json")
    @POST("/users/api/register/")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<AuthSuccess>
}