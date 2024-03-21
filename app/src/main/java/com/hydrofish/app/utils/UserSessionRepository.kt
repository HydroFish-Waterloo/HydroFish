package com.hydrofish.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.hydrofish.app.api.ApiService
import com.hydrofish.app.api.FishLevel
import com.hydrofish.app.api.PostSuccess
import com.hydrofish.app.api.WaterData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

interface IUserSessionRepository {

    val isLoggedIn: LiveData<Boolean>

    val scoreLiveData: LiveData<Int>

    fun saveWaterIntake(water: Int, date: String)

    fun getWaterIntake(): Pair<Int, String>?

    fun recordWaterData(waterData: WaterData, callback: (Boolean) -> Unit)

    fun updateScore(newScore: Int)

    fun getScore(): Int

    interface SyncScoreCallback {
        fun onSuccess(score: Int)
        fun onFailure(errorCode: Int)
    }
    fun syncScore(callback: SyncScoreCallback)

    fun saveToken(token: String)

    fun getToken(): String?

    fun saveUserName(name: String)

    fun getUserName(): String?

    fun clearData()

    fun onCleared()
}

@SuppressLint("NewApi")
class UserSessionRepository(private val context: Context, private val apiService: ApiService): IUserSessionRepository {

    private val fileName = "encrypted_prefs"
    private val keyToken = "key_token"
    private val userName = "username"

    private val _isLoggedIn = MutableLiveData<Boolean>()
    override val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences("YourSharedPreferencesName", Context.MODE_PRIVATE)
    }

    private val _scoreLiveData = MutableLiveData<Int>()
    override val scoreLiveData: LiveData<Int> get() = _scoreLiveData

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == "score") {
            val newScore = sharedPreferences.getInt(key, 1)
            _scoreLiveData.value = newScore
            Log.e("UserSessionRepository", "Score changed to: " + scoreLiveData.value.toString())
        }
    }

    init {
        val editor = preferences.edit()
        editor.clear() // This will clear all the preferences
        editor.apply()
        _scoreLiveData.value = preferences.getInt("score", 1)
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }



    override fun saveWaterIntake(water: Int, date: String) {
        val editor = preferences.edit()
        editor.putInt("KEY_WATER_INTAKE", water)
        editor.putString("KEY_DATE", date)
        editor.apply()
    }

    override fun getWaterIntake(): Pair<Int, String>? {
        val water = preferences.getInt("KEY_WATER_INTAKE", -1)
        val date = preferences.getString("KEY_DATE", null)

        return if (water != -1 && date != null) {
            Pair(water, date)
        } else {
            null
        }
    }

    override fun recordWaterData(waterData: WaterData, callback: (Boolean) -> Unit) {
        val token = getToken()
        val call = apiService.recordIntake("Token $token", waterData)
        call.enqueue(object : Callback<PostSuccess> {
            override fun onResponse(call: Call<PostSuccess>, response: Response<PostSuccess>) {
                if (response.isSuccessful) {
                    callback(true)
                } else {
                    Log.e("MainActivity", "Failed to fetch data: ${response.code()}")
                    callback(false)
                }
            }
            override fun onFailure(call: Call<PostSuccess>, t: Throwable) {
                Log.e("MainActivity", "Error occurred while fetching data", t)
                callback(false)
            }
        })
    }

    override fun onCleared() {
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

    override fun updateScore(newScore: Int) {
        preferences.edit().putInt("score", newScore).apply()
    }

    override fun getScore():Int {
        return preferences.getInt("score", 1)
    }

    override fun syncScore(callback: IUserSessionRepository.SyncScoreCallback) {
        val token = getToken()
        val score = scoreLiveData.value ?: 1
        if (token != null ) {
            val fishLevel = FishLevel(score)
            apiService.levelUp("Token $token", fishLevel).enqueue(object : Callback<PostSuccess> {
                override fun onResponse(call: Call<PostSuccess>, response: Response<PostSuccess>) {
                    if (response.isSuccessful) {
                        val responseSuccess = response.body()
                        val responseLevel = responseSuccess?.level
                        if (responseLevel != null && responseLevel != -1) {
                            updateScore(responseLevel)
                            callback.onSuccess(responseLevel)
                        }
                        else {
                            callback.onFailure(-1)
                        }
                    } else {
                        Log.e("MainActivity", "Failed to fetch data: ${response.code()}")
                        callback.onFailure(-1)
                    }
                }

                override fun onFailure(call: Call<PostSuccess>, t: Throwable) {
                    Log.e("MainActivity", "Error occurred while fetching data", t)
                    callback.onFailure(-1)
                }
            })
        }
        else {
            // Handle case where token is null
            Log.e("MainActivity", "Token is null. Unable to sync score.")
            callback.onFailure(-1)
        }
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

    override fun saveToken(token: String) {
        encryptedPrefs.edit().putString(keyToken, token).apply()
        _isLoggedIn.value = true
    }

    override fun getToken(): String? = encryptedPrefs.getString(keyToken, null)

    override fun saveUserName(name: String) {
        encryptedPrefs.edit().putString(userName, name).apply()
    }

    override fun getUserName(): String? = encryptedPrefs.getString(userName, null)

    override fun clearData() {
        encryptedPrefs.edit().remove(keyToken).apply()
        encryptedPrefs.edit().remove(userName).apply()
        _isLoggedIn.value = false
        preferences.edit().clear().apply()
    }
}