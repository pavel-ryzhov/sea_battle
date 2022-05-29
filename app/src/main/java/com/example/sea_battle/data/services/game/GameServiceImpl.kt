package com.example.sea_battle.data.services.game

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.entities.Ship
import com.example.sea_battle.utils.SpecialBufferedReader
import com.example.sea_battle.utils.SpecialBufferedWriter
import com.example.sea_battle.entities.Task
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameServiceImpl @Inject constructor() : GameService() {

    private lateinit var thisPlayerShips: List<Ship>
    private lateinit var otherPlayerShips: List<Ship>
    private lateinit var bufferedReader: SpecialBufferedReader
    private lateinit var bufferedWriter: SpecialBufferedWriter
    private lateinit var socket: Socket
    private var areFirstShipsGot = false
    private var isInterrupted = false


    override val bothPlayersAreReadyLiveData = MutableLiveData<Unit>()

    override fun start() {
        isInterrupted = false
        Thread{
            while (!isInterrupted){
                Thread(ProcessTask(Task(bufferedReader.readString()))).start()
            }
        }.start()
    }

    override fun interrupt() {
        isInterrupted = true
    }

    override fun setOtherPlayer(socket: Socket) {
        this.socket = socket
        bufferedReader = SpecialBufferedReader(socket)
        bufferedWriter = SpecialBufferedWriter(socket)
    }

    override fun notifyThisPlayerIsReadyToStart(list: List<Ship>) {
        thisPlayerShips = list
        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream).use { it.writeObject(list) }
        bufferedWriter.writeStringAndFlush("otherPlayerShips\n" + String(byteArrayOutputStream.toByteArray()))
        if (areFirstShipsGot){
            bothPlayersAreReadyLiveData.postValue(Unit)
        } else areFirstShipsGot = true
    }

    private inner class ProcessTask(private val task: Task): Runnable{
        override fun run() {
            when (task.tag){
                "otherPlayerShips" -> {
                    val byteArrayInputStream = ByteArrayInputStream(task.data.toByteArray())
                    ObjectInputStream(byteArrayInputStream).use {
                        otherPlayerShips = it.readObject() as List<Ship>
                    }
                    if (areFirstShipsGot){
                        bothPlayersAreReadyLiveData.postValue(Unit)
                    } else areFirstShipsGot = true
                }
            }
        }
    }
}