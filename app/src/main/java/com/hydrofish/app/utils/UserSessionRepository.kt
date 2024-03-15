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

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn


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

    fun clearToken() {
        encryptedPrefs.edit().remove(keyToken).apply()
        _isLoggedIn.value = false
    }


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