package com.example.sea_battle.presentation.game_result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sea_battle.data.services.game.GameService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameResultViewModel @Inject constructor(private val gameService: GameService) : ViewModel() {
    val otherPlayerExitedAfterGameResultLiveData = gameService.otherPlayerExitedAfterGameResultLiveData
    val connectionErrorLiveData = gameService.connectionErrorLiveData
    val playAgainLiveData = gameService.playAgainLiveData
    val anotherPlayerWannaPlayAgainLiveData = gameService.anotherPlayerWannaPlayAgainLiveData

    fun notifyFragmentDestroyed(){
        otherPlayerExitedAfterGameResultLiveData.postValue(null)
        connectionErrorLiveData.postValue(null)
        playAgainLiveData.postValue(null)
        anotherPlayerWannaPlayAgainLiveData.postValue(null)
    }

    fun postExit(){
        if (gameService.isJoined()) {
            viewModelScope.launch(Dispatchers.IO) {
                gameService.postExitAfterGameResult()
            }
        }
    }

    fun notifyThisPlayerWannaPlayAgain(){
        viewModelScope.launch(Dispatchers.IO) {
            gameService.notifyThisPlayerWannaPlayAgain()
        }
    }
}