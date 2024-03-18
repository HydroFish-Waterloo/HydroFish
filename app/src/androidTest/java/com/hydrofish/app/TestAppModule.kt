package com.hydrofish.app

import com.hydrofish.app.api.ApiService
import com.hydrofish.app.hilt.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

//@Module
//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [NetworkModule::class]
//)
//object TestApiModule {
//    @Provides
//    fun provideApiService(): ApiService = FakeApiService()
//}