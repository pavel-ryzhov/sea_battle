package com.example.sea_battle.presentation.start_game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sea_battle.data.services.ServerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartGameViewModel @Inject constructor(private val serverService: ServerService) : ViewModel() {

    val clientJoinedLiveData = serverService.clientJoinedLiveData

    fun startServer(name: String, timeBound: Int){
        viewModelScope.launch(Dispatchers.IO){
            serverService.startServer(name, timeBound)
        }
    }
    fun interrupt(){
        viewModelScope.launch(Dispatchers.IO) {
            serverService.interrupt()
        }
    }
}