package com.example.sea_battle.presentation.playground

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sea_battle.data.services.game.GameService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Socket
import javax.inject.Inject

@HiltViewModel
class PlaygroundViewModel @Inject constructor(val gameService: GameService) : ViewModel() {

    val bothPlayersAreReadyLiveData = gameService.bothPlayersAreReadyLiveData
    val gameFinishedLiveData = gameService.gameFinishedLiveData
    val otherPlayerExitedLiveData = gameService.otherPlayerExitedLiveData
    val connectionErrorLiveData = gameService.connectionErrorLiveData

    fun notifyFragmentDestroyed(){
        bothPlayersAreReadyLiveData.postValue(null)
        gameFinishedLiveData.postValue(null)
        otherPlayerExitedLiveData.postValue(null)
        connectionErrorLiveData.postValue(null)
        gameService.notifyFragmentDestroyed()
    }
    fun postExit() {
        viewModelScope.launch(Dispatchers.IO) {
            gameService.postExit()
        }
    }
}