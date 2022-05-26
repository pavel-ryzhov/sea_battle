package com.example.sea_battle.presentation.start_game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sea_battle.data.services.ClientService
import com.example.sea_battle.data.services.ClientServiceImpl
import com.example.sea_battle.data.services.ServerService
import com.example.sea_battle.entities.Host
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartGameViewModel @Inject constructor(private val serverService: ServerService, private val clientService: ClientService) : ViewModel() {

    val clientJoinedLiveData = serverService.clientJoinedLiveData
//    val serverIsNotAvailableLiveData = ClientServiceImpl.serverIsNotAvailableLiveData

    fun startServer(name: String, timeBound: Int, isPublic: Boolean, password: String?){
        viewModelScope.launch(Dispatchers.IO){
            serverService.startServer(name, timeBound, isPublic, password)
        }
    }
    fun interrupt(){
        viewModelScope.launch(Dispatchers.IO) {
            serverService.interrupt()
            clientService.interrupt()
        }
    }
}