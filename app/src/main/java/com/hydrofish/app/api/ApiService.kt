package com.hydrofish.app.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type:application/json")
    @POST("/users/api/login/")
    fun loginUser(@Body loginRequest: LoginRequest): Call<AuthSuccess>

    @Headers("Content-Type:application/json")
    @POST("/users/api/register/")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<AuthSuccess>

    @Headers("Content-Type:application/json")
    @POST("/hydrofish/recordintake/") // Adjust the endpoint as needed
    fun recordIntake(
        @Header("Authorization") authToken: String,
        @Body intakeData: WaterData // Adjust the data type as needed
    ): Call<PostSuccess> // Adjust the response data type as needed

    @Headers("Content-Type:application/json")
    @POST("/hydrofish/level_up/") // Adjust the endpoint as needed
    fun levelUp(
        @Header("Authorization") authToken: String,
        @Body intakeData: FishScore // Adjust the data type as needed
    ): Call<PostSuccess> // Adjust the response data type as needed
    
}