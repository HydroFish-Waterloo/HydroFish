package com.hydrofish.app.api

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.hydrofish.app.ui.composables.tabs.HydrationEntryDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val historyRetrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/") // Replace with your API base URL
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().registerTypeAdapter(object : TypeToken<List<HydrationEntry>>() {}.type, HydrationEntryDeserializer()).create()))
        .build()
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
    val historyApiService = RetrofitClient.historyRetrofit.create(ApiService::class.java)
}