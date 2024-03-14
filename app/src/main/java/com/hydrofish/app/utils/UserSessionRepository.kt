package com.hydrofish.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


interface IUserSessionRepository {
    fun saveToken(token: String)
    fun getToken(): String?
    fun saveUserName(name: String)
    fun getUserName(): String?
    fun updateScore(newScore: Int)
    fun clearData()

    val isLoggedIn: LiveData<Boolean>
    val scoreLiveData: LiveData<Int>
}

class UserSessionRepository(private val context: Context): IUserSessionRepository {

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

    override fun updateScore(newScore: Int) {
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

    override fun saveToken(token: String) {
        encryptedPrefs.edit().putString(keyToken, token).apply()
        _isLoggedIn.value = true
    }

    override fun getToken(): String? = encryptedPrefs.getString(keyToken, null)

    override fun clearData() {
        encryptedPrefs.edit().remove(keyToken).apply()
        encryptedPrefs.edit().remove(userName).apply()
        _isLoggedIn.value = false
        preferences.edit().clear().apply()
    }

    override fun saveUserName(name: String) {
        encryptedPrefs.edit().putString(userName, name).apply()
    }

    override fun getUserName(): String? = encryptedPrefs.getString(userName, null)

}