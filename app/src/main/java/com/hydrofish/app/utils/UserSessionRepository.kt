package com.hydrofish.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.hydrofish.app.api.ApiClient
import com.hydrofish.app.api.FishScore
import com.hydrofish.app.api.PostSuccess
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.Period
import java.util.*

@SuppressLint("NewApi")
class UserSessionRepository(private val context: Context) {

    private val fileName = "encrypted_prefs"
    private val keyToken = "key_token"
    private val userName = "username"

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences("YourSharedPreferencesName", Context.MODE_PRIVATE)
    }

    private val _scoreLiveData = MutableLiveData<Int>()
    val scoreLiveData: LiveData<Int> get() = _scoreLiveData

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == "score") {
            _scoreLiveData.postValue(sharedPreferences.getInt(key, 0))
        }
    }

    init {
        _scoreLiveData.value = preferences.getInt("score", 0)
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        checkResetWaterIntake()
        syncScore()
    }

    private fun checkResetWaterIntake() {
        val currentDate = LocalDate.now()
        val lastSavedDateAndWater = getWaterIntake()
        if (lastSavedDateAndWater != null) {
            val lastSavedDate = LocalDate.parse(lastSavedDateAndWater.second)
            val daysDifference = Period.between(lastSavedDate, currentDate).days
            // Check if it's been more than one day since last saved
            if (daysDifference > 1 ) {
                // Reset the currentState and save 0
                saveWaterIntake(0, currentDate.toString())
            }
        }
    }

    fun saveWaterIntake(water: Int, date: String) {
        val editor = preferences.edit()
        editor.putInt("KEY_WATER_INTAKE", water)
        editor.putString("KEY_DATE", date)
        editor.apply()
    }

    fun getWaterIntake(): Pair<Int, String>? {
        val water = preferences.getInt("KEY_WATER_INTAKE", -1)
        val date = preferences.getString("KEY_DATE", null)

        return if (water != -1 && date != null) {
            Pair(water, date)
        } else {
            null
        }
    }

    fun onCleared() {
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

//    val score: LiveData<Int> = MutableLiveData<Int>().apply {
//        value = preferences.getInt("score", 0)
//        preferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
//            if (key == "score") {
//                postValue(sharedPreferences.getInt(key, 0))
//            }
//        }
//    }

    fun updateScore(newScore: Int) {
        preferences.edit().putInt("score", newScore).apply()
    }

    fun syncScore(): Int {
        val token = getToken()
        if (token != null ) {
            val score = scoreLiveData.value?.let { FishScore(it) }
            score?.let<FishScore, Call<PostSuccess>> {
                ApiClient.apiService.levelUp(
                    "Token $token",
                    it
                )
            }?.enqueue(object : Callback<PostSuccess> {
                override fun onResponse(call: Call<PostSuccess>, response: Response<PostSuccess>) {
                    if (response.isSuccessful) {
                        val responseSuccess = response.body()
                        val responseLevel = responseSuccess?.level
                        if (responseLevel != null && responseLevel != -1) {
                            updateScore(responseLevel)
                        }
                    } else {
                        Log.e("MainActivity", "Failed to fetch data: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<PostSuccess>, t: Throwable) {
                    Log.e("MainActivity", "Error occurred while fetching data", t)
                }
            })
        }
        return -1
    }




    private val encryptedPrefs: SharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            fileName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    init {
        _isLoggedIn.value = getToken() != null
    }

    fun saveToken(token: String) {
        encryptedPrefs.edit().putString(keyToken, token).apply()
        _isLoggedIn.value = true
    }

    fun getToken(): String? = encryptedPrefs.getString(keyToken, null)

    fun clearData() {
        encryptedPrefs.edit().remove(keyToken).apply()
        encryptedPrefs.edit().remove(userName).apply()
        _isLoggedIn.value = false
        preferences.edit().clear().apply()
    }

    fun saveUserName(name: String) {
        encryptedPrefs.edit().putString(userName, name).apply()
    }

    fun getUserName(): String? = encryptedPrefs.getString(userName, null)


//    fun isLoggedIn(): Boolean {
//        return SecureStorage.getToken(context) != null
//    }
//
//    fun saveToken(token: String) {
//        SecureStorage.saveToken(context,token)
//    }
//
//    fun clearToken() {
//        SecureStorage.clearToken(context)
//    }
}