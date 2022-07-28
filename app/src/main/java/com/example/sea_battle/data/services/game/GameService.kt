package com.example.sea_battle.data.services.game

import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.entities.Ship
import java.net.Socket

abstract class GameService {

    abstract val bothPlayersAreReadyLiveData: MutableLiveData<Int?>
    abstract val clickLiveData: MutableLiveData<IntArray?>
    abstract val gameFinishedLiveData: MutableLiveData<Pair<Int, List<Ship>>?>
    abstract val otherPlayerExitedLiveData: MutableLiveData<Unit>
    abstract val otherPlayerExitedAfterGameResultLiveData: MutableLiveData<Unit>
    abstract val connectionErrorLiveData: MutableLiveData<Unit>
    abstract val playAgainLiveData: MutableLiveData<Unit>
    abstract val anotherPlayerWannaPlayAgainLiveData: MutableLiveData<Unit>
    abstract val otherPlayerShips: List<Ship>
    abstract val thisPlayerShips: List<Ship>

    abstract fun executeClick(coords: IntArray)
    abstract fun start()
    abstract fun finishGame(arg: Int)
    abstract fun interrupt()
    abstract fun setOtherPlayer(socket: Socket)
    abstract fun postExit()
    abstract fun postExitAfterGameResult()
    abstract fun notifyThisPlayerIsReadyToStart(list: List<Ship>)
    abstract fun isJoined(): Boolean
    abstract fun notifyFragmentDestroyed()
    abstract fun notifyThisPlayerWannaPlayAgain()
    abstract fun restart()
}