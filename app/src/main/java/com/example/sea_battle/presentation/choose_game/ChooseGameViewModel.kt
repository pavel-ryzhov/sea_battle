package com.example.sea_battle.presentation.choose_game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sea_battle.data.services.client.ClientService
import com.example.sea_battle.data.services.client.ClientServiceImpl
import com.example.sea_battle.entities.Host
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseGameViewModel @Inject constructor(private val clientService: ClientService) : ViewModel() {
    val newServerDetectedLiveData = clientService.newServerDetectedLiveData
    val serverIsNotAvailableLiveData = clientService.serverIsNotAvailableLiveData

    fun notifyClientJoinedGame(host: Host): Boolean{
        return clientService.notifyClientJoinedGame(host)
    }
    fun findServers(clientName: String){
        viewModelScope.launch(Dispatchers.IO){
            clientService.findServers(clientName, 200, 10)
        }
    }
    fun interrupt(){
        viewModelScope.launch(Dispatchers.IO){
            clientService.interrupt()
        }
    }
    fun notifyFragmentDestroyed(){
        newServerDetectedLiveData.postValue(null)
        serverIsNotAvailableLiveData.postValue(null)
    }
}