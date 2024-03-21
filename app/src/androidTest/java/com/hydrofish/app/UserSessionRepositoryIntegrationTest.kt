package com.hydrofish.app

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

import androidx.test.core.app.ApplicationProvider
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.PostSuccess
import com.hydrofish.app.api.WaterData
import com.hydrofish.app.utils.IUserSessionRepository
import com.hydrofish.app.utils.UserSessionRepository
import org.junit.Before
import org.junit.Rule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import retrofit2.Call
import retrofit2.Response

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UserSessionRepositoryIntegrationTest {

    private lateinit var userSessionRepository: IUserSessionRepository
    private lateinit var apiService: ApiService
    private lateinit var context: Context
    private lateinit var preferences: SharedPreferences

    @Before
    fun setUp() {
        context = mockk<Context>(relaxed = true)

        apiService = mockk()
        val encryptedPrefs: SharedPreferences = mockk()

        // Set up behavior for SharedPreferences mock
        every { encryptedPrefs.getString("key_token", null) } returns "dummyToken"

        // Mock the creation of EncryptedSharedPreferences to return the mock
        mockkStatic(EncryptedSharedPreferences::class)
        every {
            EncryptedSharedPreferences.create(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns encryptedPrefs

        userSessionRepository = UserSessionRepository(context, apiService)

        // Define the behavior of getToken() method
        val token = "dummyToken"
        every { userSessionRepository.getToken() } returns token
    }

    @Test
    fun testRecordWaterData() {
        // Given
        val waterData = WaterData("2024-03-20", 100)
        val token = "dummyToken"
        val postSuccess = PostSuccess(status = "Success", message = "Water intake recorded", level = 1)
        val mockCall: Call<PostSuccess> = mockk(relaxed = true)
        every { mockCall.execute() } returns Response.success(postSuccess)

        every { apiService.recordIntake("Token $token", waterData) } returns mockCall

        // When
        userSessionRepository.recordWaterData(waterData) { isSuccess ->
            if (isSuccess) {
                // Handle success scenario
            } else {
                // Handle failure scenario
            }
        }

        // Then
        verify { apiService.recordIntake("Token $token", waterData) }
        verify { mockCall.enqueue(any()) }
    }
}