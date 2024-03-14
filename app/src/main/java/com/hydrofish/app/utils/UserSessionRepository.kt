package com.hydrofish.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

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