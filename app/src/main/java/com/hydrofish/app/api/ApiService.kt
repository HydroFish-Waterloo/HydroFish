package com.hydrofish.app.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type:application/json")
    @POST("/users/api/login/")
    fun loginUser(@Body loginRequest: LoginRequest): Call<AuthSuccess>

    @Headers("Content-Type:application/json")
    @POST("/users/api/register/")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<AuthSuccess>

    @GET("hydrofish/get_history_monthly/") // Replace "endpoint" with your API endpoint
    // fun getHydrationData(@HeaderMap headers: Map<String, String?>): Call<List<HydrationEntry>>
    fun getHydrationData(@HeaderMap headers: Map<String, String?>): Call<DataResponse>
}