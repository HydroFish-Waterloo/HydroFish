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
import com.hydrofish.app.api.FishLevel
import com.hydrofish.app.api.PostSuccess
import com.hydrofish.app.api.WaterData
import com.hydrofish.app.utils.IUserSessionRepository
import com.hydrofish.app.utils.UserSessionRepository
import org.junit.Before
import org.junit.Rule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import retrofit2.Call
import retrofit2.Callback
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

    }

    @Test
    fun testRecordWaterDataSuccess() {
        // Given
        val waterData = WaterData("2024-03-20", 100)
        val token = "dummyToken"
        val postSuccess = PostSuccess(status = "Success", message = "Water intake recorded", level = 1)
        val mockCall: Call<PostSuccess> = mockk(relaxed = true)
        val slot = slot<Callback<PostSuccess>>()
        every { mockCall.enqueue(capture(slot)) } answers {
            // Invoke the captured callback with isSuccess as true
            slot.captured.onResponse(mockCall, Response.success(postSuccess))
        }
        
        every { userSessionRepository.getToken() } returns token
        every { apiService.recordIntake("Token $token", waterData) } returns mockCall

        // When
        var isSuccessCaptured: Boolean? = null
        userSessionRepository.recordWaterData(waterData) { isSuccess ->
            if (isSuccess) {
                isSuccessCaptured = isSuccess
                // Handle success scenario
            } else {
                // Handle failure scenario
            }
        }

        // Then
        verify { apiService.recordIntake("Token $token", waterData) }
        verify { mockCall.enqueue(any()) }

        // Verify that the callback was invoked with true (success)
        assert(isSuccessCaptured == true)
    }
    @Test
    fun testRecordWaterDataFailure() {
        // Given
        val waterData = WaterData("2024-03-20", 100)
        val token = "dummyToken"
        val mockCall: Call<PostSuccess> = mockk(relaxed = true)
        val slot = slot<Callback<PostSuccess>>()
        every { mockCall.enqueue(capture(slot)) } answers {
            // Simulate failure by invoking onFailure callback
            slot.captured.onFailure(mockCall, mockk())
        }

        every { userSessionRepository.getToken() } returns token
        every { apiService.recordIntake("Token $token", waterData) } returns mockCall

        // When
        var isSuccessCaptured: Boolean? = null
        userSessionRepository.recordWaterData(waterData) { isSuccess ->
            isSuccessCaptured = isSuccess
            // Handle success or failure scenario
        }

        // Then
        verify { apiService.recordIntake("Token $token", waterData) }
        verify { mockCall.enqueue(any()) }

        // Verify that the callback was invoked with false (failure)
        assert(isSuccessCaptured == false)
    }
    @Test
    fun testRecordWaterDataNullToken() {
        // Given
        val waterData = WaterData("2024-03-20", 100)
        every { userSessionRepository.getToken() } returns null

        // When
        var isSuccessCaptured: Boolean? = null
        userSessionRepository.recordWaterData(waterData) { isSuccess ->
            isSuccessCaptured = isSuccess
        }

        // Then
        // Verify that getToken() was called
        verify { userSessionRepository.getToken() }

        // Verify that the callback was invoked with false
        assert(isSuccessCaptured == false)
    }
    @Test
    fun testSyncScoreSuccess() {
        // Given
        val score = 10 // Example score value
        val token = "dummyToken"
        val postSuccess = PostSuccess(status = "Success", message = "Level up successful", level = 10)
        val mockCall: Call<PostSuccess> = mockk(relaxed = true)
        val slot = slot<Callback<PostSuccess>>()
        every { mockCall.enqueue(capture(slot)) } answers {
            // Invoke the captured callback with isSuccess as true
            slot.captured.onResponse(mockCall, Response.success(postSuccess))
        }
        every { userSessionRepository.getToken() } returns token
        every { userSessionRepository.getScore() } returns score
        every { apiService.levelUp("Token $token", any()) } returns mockCall

        // When
        var capturedLevel: Int? = null
        userSessionRepository.syncScore(object : IUserSessionRepository.SyncScoreCallback {
            override fun onSuccess(score: Int) {
                capturedLevel = score
            }

            override fun onFailure(errorCode: Int) {
                // Handle failure scenario if needed
            }
        })

        // Then
        verify { apiService.levelUp(eq("Token $token"), eq(FishLevel(score))) }
        assert(capturedLevel == 10)
    }
    @Test
    fun testSyncScoreFailure() {
        // Given
        val token = "dummyToken"
        val score = 10 // Example score value
        val errorCode = -1 // Example error code for failure
        val mockCall: Call<PostSuccess> = mockk(relaxed = true)
        val slot = slot<Callback<PostSuccess>>()
        every { mockCall.enqueue(capture(slot)) } answers {
            // Simulate a failure by invoking the onFailure callback
            slot.captured.onFailure(mockCall, Throwable("Failed to sync score"))
        }
        every { userSessionRepository.getToken() } returns token
        every { userSessionRepository.getScore() } returns score
        every { apiService.levelUp("Token $token", any()) } returns mockCall

        // When
        var capturedErrorCode: Int? = null
        userSessionRepository.syncScore(object : IUserSessionRepository.SyncScoreCallback {
            override fun onSuccess(score: Int) {
                // Handle success scenario
            }

            override fun onFailure(errorCode: Int) {
                capturedErrorCode = errorCode
            }
        })

        // Then
        verify { apiService.levelUp(eq("Token $token"), eq(FishLevel(score))) }
        assert(capturedErrorCode == errorCode)
    }

    @Test
    fun testSyncScoreTokenNull() {
        // Given
        val token: String? = null // Simulating null token
        val errorCode = -1 // Example error code for failure
        every { userSessionRepository.getToken() } returns token

        // When
        var capturedErrorCode: Int? = null
        userSessionRepository.syncScore(object : IUserSessionRepository.SyncScoreCallback {
            override fun onSuccess(score: Int) {
                // Handle success scenario
            }

            override fun onFailure(errorCode: Int) {
                capturedErrorCode = errorCode
            }
        })

        // Then
        assert(capturedErrorCode == errorCode)
    }
}