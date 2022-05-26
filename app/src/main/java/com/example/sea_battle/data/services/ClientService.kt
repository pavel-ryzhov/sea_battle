package com.example.sea_battle.data.services

import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.entities.Host
import java.net.Socket
import javax.inject.Inject

abstract class ClientService{
    abstract val newServerDetectedLiveData: MutableLiveData<Host>
//    abstract val serverIsNotAvailableLiveData: MutableLiveData<Host>
    abstract val netScanned: MutableLiveData<Unit>

    abstract fun findServers(clientName: String, nThreads: Int, nPorts: Int)
    abstract fun interrupt()
    abstract fun getAllDetectedServers(): List<Host>

    protected abstract fun verifyServer(clientName: String, socket: Socket): Boolean
}