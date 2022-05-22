package com.example.sea_battle.data.services

import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.entities.Client
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

abstract class ServerService{

    abstract val clientJoinedLiveData: MutableLiveData<Client>

    abstract fun interrupt()
    abstract fun startServer(name: String, timeBound: Int)

    protected abstract fun verifyUser(name: String, timeBound: Int, socket: Socket) : Boolean
}