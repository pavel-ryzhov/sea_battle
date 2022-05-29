package com.example.sea_battle.data.services.game

import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.entities.Ship
import java.net.Socket

abstract class GameService {

    abstract val bothPlayersAreReadyLiveData: MutableLiveData<Unit>

    abstract fun start()
    abstract fun interrupt()
    abstract fun setOtherPlayer(socket: Socket)
    abstract fun notifyThisPlayerIsReadyToStart(list: List<Ship>)
}