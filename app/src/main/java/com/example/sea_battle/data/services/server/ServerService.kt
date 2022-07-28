package com.example.sea_battle.data.services.server

import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.entities.Client
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

abstract class ServerService{

    abstract val clientJoinedLiveData: MutableLiveData<Client?>

    abstract fun interrupt()
    abstract fun close()
    abstract fun startServer(name: String, timeBound: Int, isPublic: Boolean, password: String?)
    abstract fun isClientJoined(): Boolean
    abstract fun notifyClientJoined(client: Client)

    protected abstract fun verifyUser(name: String, timeBound: Int, isPublic: Boolean, password: String?, socket: Socket) : Boolean
}