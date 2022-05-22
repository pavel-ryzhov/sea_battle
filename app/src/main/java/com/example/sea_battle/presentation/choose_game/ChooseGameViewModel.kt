package com.example.sea_battle.presentation.choose_game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sea_battle.data.services.ClientService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseGameViewModel @Inject constructor(private val clientService: ClientService) : ViewModel() {
    val newServerDetectedLiveData = clientService.newServerDetectedLiveData
    val netScanned = clientService.netScanned

    fun findServers(clientName: String){
        viewModelScope.launch(Dispatchers.IO){
            clientService.findServers(clientName, 100, 10)
        }
    }
    fun interrupt(){
        viewModelScope.launch(Dispatchers.IO){
            clientService.interrupt()
        }
    }
}