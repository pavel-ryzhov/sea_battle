package com.example.sea_battle.presentation.start_game

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sea_battle.data.services.client.ClientService
import com.example.sea_battle.data.services.game.GameService
import com.example.sea_battle.data.services.server.ServerService
import com.example.sea_battle.entities.Client
import com.example.sea_battle.entities.Ship
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Socket
import javax.inject.Inject

@HiltViewModel
class StartGameViewModel @Inject constructor(
    private val serverService: ServerService,
    private val clientService: ClientService,
    private val gameService: GameService
) : ViewModel() {

    val clientJoinedLiveData = serverService.clientJoinedLiveData
    val userIsReadyLiveData = MutableLiveData<Boolean>()
    val otherPlayerExitedLiveData = gameService.otherPlayerExitedLiveData
    val connectionErrorLiveData = gameService.connectionErrorLiveData

    fun startServer(name: String, timeBound: Int, isPublic: Boolean, password: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            serverService.startServer(name, timeBound, isPublic, password)
        }
    }

    fun setOtherPlayer(socket: Socket) {
        gameService.setOtherPlayer(socket)
    }

    fun interrupt() {
        serverService.interrupt()
        clientService.interrupt()
    }

    fun notifyThisPlayerIsReadyToStart(ships: List<Ship>) {
        viewModelScope.launch(Dispatchers.IO) {
            gameService.notifyThisPlayerIsReadyToStart(ships)
        }
    }

    fun restart() = gameService.restart()

    fun close() {
        serverService.close()
        clientService.close()
    }

    fun checkIfUserIsReady(ships: List<Ship>) {
        viewModelScope.launch {
            userIsReadyLiveData.postValue(ships.size == 10)
        }
    }

    fun startListening(socket: Socket) {
        viewModelScope.launch {
            gameService.setOtherPlayer(socket)
            gameService.start()
        }
    }

    fun isOtherPlayerJoined(isHost: Boolean) =
        gameService.isJoined() && (if (isHost) serverService.isClientJoined() else clientService.isJoinedToServer())

    fun postExit() {
        viewModelScope.launch(Dispatchers.IO) {
            gameService.postExit()
        }
    }

    fun notifyFragmentDestroyed() {
        clientJoinedLiveData.postValue(null)
        otherPlayerExitedLiveData.postValue(null)
        connectionErrorLiveData.postValue(null)
    }
    fun notifyClientJoined(client: Client){
        serverService.notifyClientJoined(client)
    }
}