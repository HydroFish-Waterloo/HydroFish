package com.hydrofish.app

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hydrofish.app.animations.AnimationGroup
import com.hydrofish.app.api.ApiClient
import com.hydrofish.app.api.PostSuccess
import com.hydrofish.app.api.WaterData
import com.hydrofish.app.ui.HydroFishUIState
import com.hydrofish.app.utils.UserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.Period

class HydroFishViewModel(private val userSessionRepository: UserSessionRepository): ViewModel() {
    // backing property to avoid updates from other classes
    private val _uiState = MutableStateFlow(HydroFishUIState())
    val uiState: StateFlow<HydroFishUIState> = _uiState.asStateFlow()
    val scoreLiveData: LiveData<Int> = userSessionRepository.scoreLiveData


    init {
        initWaterBar()
        initScore()
    }

    private fun initWaterBar() {
        // Get last saved date and water intake
        val lastSavedDateAndWater = userSessionRepository.getWaterIntake()
        if (lastSavedDateAndWater != null) {
            // Get the current value of _uiState
            _uiState.update { currentState ->
                currentState.copy(
                    dailyWaterConsumedML = lastSavedDateAndWater.first
                )
            }
        }
    }

    private fun initScore() {
        val currentScore = userSessionRepository.getScore()
        _uiState.update { currentState ->
            currentState.copy(
                fishScore = currentScore
            )
        }
        val responseLevel: Int = userSessionRepository.syncScore()
        if (responseLevel != -1) {
            _uiState.update { currentState ->
                currentState.copy(
                    fishScore = responseLevel
                )
            }
        }
        uiState.value.animationGroupPositionHandler.prepForPopulation()
    }

    @SuppressLint("NewApi")
    fun increaseWaterLevel(amt: Int) {
        Log.d("Score", uiState.value.fishScore.toString())
        if (amt < 0) throw Exception("Cannot Increase Water By Negative Value");
        _uiState.update { currentState ->
            currentState.copy(
                dailyWaterConsumedML = amt + currentState.dailyWaterConsumedML,
            )
        }
        val currentDate = LocalDate.now()
        Log.d("currentDate", currentDate.toString())
        userSessionRepository.saveWaterIntake(_uiState.value.dailyWaterConsumedML, currentDate.toString())
        val token = userSessionRepository.getToken()
        if (token != null ) {

            val waterData = WaterData(currentDate.toString(), amt)
            val call = ApiClient.apiService.recordIntake("Token " + token, waterData)
            call.enqueue(object : Callback<PostSuccess> {
                override fun onResponse(call: Call<PostSuccess>, response: Response<PostSuccess>) {
                    if (response.isSuccessful) {
                        response.body()?.message?.let { Log.d("whateverIwant", it) }
                    } else {
                        Log.e("MainActivity", "Failed to fetch data: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<PostSuccess>, t: Throwable) {
                    Log.e("MainActivity", "Error occurred while fetching data", t)
                }
            })
        }
    }

    fun levelUpdate() {
        val currentScore = userSessionRepository.getScore()
        uiState.value.animationGroupPositionHandler.prepForPopulation()
        _uiState.update { currentState ->
            currentState.copy(
                fishScore = currentScore
            )
        }
    }

    fun levelUp() {
        val currentScore = userSessionRepository.getScore()
        val newScore = currentScore + 1
        uiState.value.animationGroupPositionHandler.prepForPopulation()
        userSessionRepository.updateScore(newScore)
        _uiState.update { currentState ->
            currentState.copy(
                fishScore = newScore
            )
        }
        val responseLevel: Int = userSessionRepository.syncScore()
        if (responseLevel != -1) {
            _uiState.update { currentState ->
                currentState.copy(
                    fishScore = responseLevel
                )
            }
        }
        //Testing Purpose
//        _uiState.update { currentState ->
//            currentState.copy(
//                dailyWaterConsumedML = 0,
//            )
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkResetWaterIntake() {
        val currentDate = LocalDate.now()
        val lastSavedDateAndWater = userSessionRepository.getWaterIntake()
        Log.d("currentDate", currentDate.toString())
        if (lastSavedDateAndWater != null) {
            val lastSavedDate = LocalDate.parse(lastSavedDateAndWater.second)
            val daysDifference = Period.between(lastSavedDate, currentDate).days
            // Check if it's been more than one day since last saved
            Log.d("lastSavedDate", lastSavedDate.toString())
            if (daysDifference >= 1 ) {
                Log.d("daysDifference", daysDifference.toString())
                // Reset the currentState and save 0
                userSessionRepository.saveWaterIntake(0, currentDate.toString())
                _uiState.update { currentState ->
                    currentState.copy(
                        dailyWaterConsumedML = 0
                    )
                }
            }
        }
    }

    fun getAllFish(): List<AnimationGroup> {
        return uiState.value.animationGroupPositionHandler.getAllFish(uiState.value.fishScore)
    }
}
