package com.example.sea_battle.data.services.game

import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.entities.Ship
import com.example.sea_battle.entities.Task
import com.example.sea_battle.utils.SpecialBufferedReader
import com.example.sea_battle.utils.SpecialBufferedWriter
import com.example.sea_battle.views.PlaygroundView.Companion.OTHER_PLAYER_TURN
import com.example.sea_battle.views.PlaygroundView.Companion.OTHER_PLAYER_VICTORY
import com.example.sea_battle.views.PlaygroundView.Companion.THIS_PLAYER_TURN
import com.example.sea_battle.views.PlaygroundView.Companion.THIS_PLAYER_VICTORY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class GameServiceImpl @Inject constructor() : GameService() {


    companion object {
        private const val OTHER_PLAYER_SHIPS_TAG = "otherPlayerShips"
        private const val FIRST_TURN_TAG = "firstTurn"
        private const val CLICK_TAG = "click"
        private const val GAME_FINISHED_TAG = "gameFinished"
        private const val PLAYER_EXITED_TAG = "playerExited"
        private const val PLAYER_EXITED_AFTER_GAME_RESULT_TAG = "playerExitedAfterGameResult"
        private const val PLAY_AGAIN_TAG = "playAgain"

        private val EMPTY_DATA = byteArrayOf()
    }


    override lateinit var thisPlayerShips: List<Ship>
    override lateinit var otherPlayerShips: List<Ship>

    private lateinit var bufferedReader: SpecialBufferedReader
    private lateinit var bufferedWriter: SpecialBufferedWriter
    private lateinit var socket: Socket
    private var areFirstShipsGot = false
    private var isInterrupted = false
    private var gameFinished = false
    private var firstTurn = -1
    private var isJoined = true
    private var connectionErrorExpected = false
    private var playAgain = false

    override val clickLiveData = MutableLiveData<IntArray?>()
    override val bothPlayersAreReadyLiveData = MutableLiveData<Int?>()
    override val gameFinishedLiveData = MutableLiveData<Pair<Int, List<Ship>>?>()
    override val otherPlayerExitedLiveData = MutableLiveData<Unit>()
    override val otherPlayerExitedAfterGameResultLiveData = MutableLiveData<Unit>()
    override val connectionErrorLiveData = MutableLiveData<Unit>()
    override val playAgainLiveData = MutableLiveData<Unit>()
    override val anotherPlayerWannaPlayAgainLiveData = MutableLiveData<Unit>()

    override fun executeClick(coords: IntArray) {
        bufferedWriter.writeTask(
            Task(
                CLICK_TAG,
                "${coords.component1()}:${coords.component2()}".toByteArray()
            )
        )
    }

    override fun notifyThisPlayerWannaPlayAgain() {
        bufferedWriter.writeTask(Task(
            PLAY_AGAIN_TAG,
            EMPTY_DATA
        ))
        if (playAgain){
            playAgainLiveData.postValue(Unit)
        }else{
            playAgain = true
        }
    }

    override fun postExit() {
        bufferedWriter.writeTask(Task(
            PLAYER_EXITED_TAG,
            EMPTY_DATA
        ))
        connectionErrorExpected = true
    }

    override fun postExitAfterGameResult() {
        bufferedWriter.writeTask(Task(
            PLAYER_EXITED_AFTER_GAME_RESULT_TAG,
            EMPTY_DATA
        ))
        connectionErrorExpected = true
    }

    override fun start() {
        playAgain = false
        areFirstShipsGot = false
        connectionErrorExpected = false
        isInterrupted = false
        isJoined = true
        gameFinished = false
        Thread {
            try {
                while (!isInterrupted) {
                    Thread(ProcessTask(bufferedReader.readTask())).start()
                }
            }catch (e: Exception){
                isJoined = false
                if (!connectionErrorExpected)
                    connectionErrorLiveData.postValue(Unit)
            }
        }.start()
    }

    override fun notifyFragmentDestroyed() {
        clickLiveData.postValue(null)
    }

    override fun interrupt() {
        isInterrupted = true
    }

    override fun isJoined() = isJoined

    override fun setOtherPlayer(socket: Socket) {
        this.socket = socket
        bufferedReader = SpecialBufferedReader(socket)
        bufferedWriter = SpecialBufferedWriter(socket)
    }

    override fun notifyThisPlayerIsReadyToStart(list: List<Ship>) {
        thisPlayerShips = list
        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream).use { it.writeObject(list) }
        bufferedWriter.writeTask(Task(OTHER_PLAYER_SHIPS_TAG, byteArrayOutputStream.toByteArray()))
        if (areFirstShipsGot) {
            bothPlayersAreReadyLiveData.postValue(firstTurn)
        } else {
            areFirstShipsGot = true
        }
    }

    override fun restart() {
        playAgain = false
        areFirstShipsGot = false
        connectionErrorExpected = false
        isInterrupted = false
        isJoined = true
        gameFinished = false
    }

    override fun finishGame(arg: Int) {
        if (!gameFinished) {
            CoroutineScope(Dispatchers.IO + Job()).launch {
                bufferedWriter.writeTask(Task(GAME_FINISHED_TAG, arg.toString().toByteArray()))
                gameFinishedLiveData.postValue(Pair(arg, otherPlayerShips))
            }
        } else gameFinished = true
    }

    private inner class ProcessTask(private val task: Task) : Runnable {
        override fun run() {
            when (task.tag) {
                OTHER_PLAYER_SHIPS_TAG -> {
                    val byteArrayInputStream = ByteArrayInputStream(task.data)
                    ObjectInputStream(byteArrayInputStream).let {
                        otherPlayerShips = it.readObject() as List<Ship>
                    }
                    if (areFirstShipsGot) {
                        bothPlayersAreReadyLiveData.postValue(firstTurn)
                    } else {
                        areFirstShipsGot = true
                        val ft = Math.random().roundToInt()
                        bufferedWriter.writeTask(
                            Task(
                                FIRST_TURN_TAG,
                                byteArrayOf((if (ft == OTHER_PLAYER_TURN) THIS_PLAYER_TURN else OTHER_PLAYER_TURN).toByte())
                            )
                        )
                        firstTurn = ft
                    }
                }
                FIRST_TURN_TAG -> {
                    firstTurn = task.data.first().toInt()
                }
                CLICK_TAG -> {
                    val coords = String(task.data).split(":")
                    clickLiveData.postValue(
                        intArrayOf(
                            coords.component1().toInt(),
                            coords.component2().toInt()
                        )
                    )
                }
                GAME_FINISHED_TAG -> {
                    val arg = String(task.data).toInt()
                    gameFinishedLiveData.postValue(
                        Pair(
                            when (arg) {
                                THIS_PLAYER_VICTORY -> OTHER_PLAYER_VICTORY
                                OTHER_PLAYER_VICTORY -> THIS_PLAYER_VICTORY
                                else -> arg
                            },
                            otherPlayerShips
                        )
                    )
                }
                PLAYER_EXITED_TAG -> {
                    connectionErrorExpected = true
                    isJoined = false
                    isInterrupted = true
                    otherPlayerExitedLiveData.postValue(Unit)
                    socket.close()
                }
                PLAYER_EXITED_AFTER_GAME_RESULT_TAG -> {
                    connectionErrorExpected = true
                    isJoined = false
                    isInterrupted = true
                    otherPlayerExitedAfterGameResultLiveData.postValue(Unit)
                    socket.close()
                }
                PLAY_AGAIN_TAG -> {
                    if (playAgain){
                        playAgainLiveData.postValue(Unit)
                    }else{
                        playAgain = true
                        anotherPlayerWannaPlayAgainLiveData.postValue(Unit)
                    }
                }
            }
        }
    }
}