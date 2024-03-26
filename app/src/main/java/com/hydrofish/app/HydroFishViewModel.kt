package com.hydrofish.app

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hydrofish.app.animations.AnimationGroup
import com.hydrofish.app.api.WaterData
import com.hydrofish.app.ui.HydroFishUIState
import com.hydrofish.app.utils.IUserSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.Period

class HydroFishViewModel(private val userSessionRepository: IUserSessionRepository): ViewModel() {
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
        val lastSavedDateAndWater = userSessionRepository.getWaterIntake() ?: return

        // Get the current value of _uiState
        _uiState.update { currentState ->
            currentState.copy(
                dailyWaterConsumedML = lastSavedDateAndWater.first
            )
        }
    }

    private fun initScore() {
        var currentScore = userSessionRepository.getScore()
        _uiState.update { currentState ->
            currentState.copy(
                fishScore = currentScore
            )
        }
        userSessionRepository.syncScore(object : IUserSessionRepository.SyncScoreCallback {
            override fun onSuccess(score: Int) {
                if (score == -1) {
                    // TODO - Handle failure scenario if needed
                    return
                }

                currentScore  = userSessionRepository.getScore()
                if (score == currentScore) return

                _uiState.update { currentState ->
                    currentState.copy(
                        fishScore = score
                    )
                }
            }

            override fun onFailure(errorCode: Int) {
                // TODO - Handle failure
            }
        })
    }

    @SuppressLint("NewApi")
    fun increaseWaterLevel(amt: Int) {
        if (amt < 0) throw Exception("Cannot Increase Water By Negative Value");

        _uiState.update { currentState ->
            currentState.copy(
                dailyWaterConsumedML = amt + currentState.dailyWaterConsumedML,
            )
        }

        // save current water consumed
        val currentDate = LocalDate.now()
        userSessionRepository.saveWaterIntake(uiState.value.dailyWaterConsumedML, currentDate.toString())
        userSessionRepository.getToken() ?: return

        // save water amount increased
        val waterData = WaterData(currentDate.toString(), amt)
        userSessionRepository.recordWaterData(waterData) { isSuccess ->
            if (isSuccess) {
                // TODO - Handle success scenario
            } else {
                // TODO - Handle failure scenario
            }
        }
    }

    fun updateFishModels() {
        val currentScore = userSessionRepository.getScore()
        _uiState.update { currentState ->
            currentState.copy(
                fishScore = currentScore
            )
        }
        uiState.value.animationGroupPositionHandler.prepForPopulation()
    }

    fun levelUp() {
        var currentScore = userSessionRepository.getScore()
        val newScore = currentScore + 1

        userSessionRepository.updateScore(newScore)
        userSessionRepository.syncScore(object : IUserSessionRepository.SyncScoreCallback {
            override fun onSuccess(score: Int) {
                if (score == -1) {
                    // TODO - Handle failure scenario
                    return
                }

                currentScore  = userSessionRepository.getScore()
                if (score == currentScore) return

                _uiState.update { currentState ->
                    currentState.copy(
                        fishScore = score
                    )
                }
            }

            override fun onFailure(errorCode: Int) {
                // TODO - Handle failure
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkResetWaterIntake() {
        val currentDate = LocalDate.now()
        val lastSavedDateAndWater = userSessionRepository.getWaterIntake() ?: return

        // Check if it's been more than one day since last saved
        val lastSavedDate = LocalDate.parse(lastSavedDateAndWater.second)
        val daysDifference = Period.between(lastSavedDate, currentDate).days
        if (daysDifference == 0) return

        // Reset the currentState and save 0
        userSessionRepository.saveWaterIntake(0, currentDate.toString())
        _uiState.update { currentState ->
            currentState.copy(
                dailyWaterConsumedML = 0
            )
        }
    }

    fun getAllFish(): List<AnimationGroup> {
        return uiState.value.animationGroupPositionHandler.getAllFish(uiState.value.fishScore)
    }
}
