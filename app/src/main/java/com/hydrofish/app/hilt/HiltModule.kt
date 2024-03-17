package com.hydrofish.app.hilt

import android.content.Context
import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.RetrofitClient
import com.hydrofish.app.utils.IUserSessionRepository
import com.hydrofish.app.utils.UserSessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideUserSessionRepository(@ApplicationContext context: Context): IUserSessionRepository {
        return UserSessionRepository(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return RetrofitClient.retrofit.create(ApiService::class.java)
    }
}
