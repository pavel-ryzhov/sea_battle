package com.example.sea_battle.data.services.server

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.BuildConfig
import com.example.sea_battle.data.services.client.ClientServiceImpl
import com.example.sea_battle.entities.Client
import com.example.sea_battle.utils.SpecialBufferedReader
import com.example.sea_battle.utils.SpecialBufferedWriter
import java.io.IOException
import java.net.BindException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import javax.inject.Inject

class ServerServiceImpl @Inject constructor() : ServerService() {

    companion object {
        private const val START_PORT = 5000;
    }

    override val clientJoinedLiveData = MutableLiveData<Client>()

    private val verifiedClients = mutableListOf<Socket>()
    private var isInterrupted = false

    override fun startServer(name: String, timeBound: Int, isPublic: Boolean, password: String?) {
        var port = START_PORT
        while (!isInterrupted) {
            val serverSocket = ServerSocket(port, 0, ClientServiceImpl.getCurrentIp())
            try {
                val socket = serverSocket.apply {
                    Log.d("ssss", this.toString())
                    soTimeout = 3000 }.accept()

                Thread{
                    if (verifyUser(name, timeBound, isPublic, password, socket)) {
                        synchronized(verifiedClients){
                            verifiedClients.add(socket)
                        }
                    } else {
                        socket.close()
                    }
                }.start()

            } catch (e: SocketTimeoutException) {
            } catch (e: BindException) {
                port++
            } catch (e: IOException) {
                e.printStackTrace()
            }finally {
                serverSocket.close()
            }
        }
    }

    override fun verifyUser(name: String, timeBound: Int, isPublic: Boolean, password: String?, socket: Socket): Boolean {
        try {
            val bufferedWriter = SpecialBufferedWriter(socket)
            val bufferedReader = SpecialBufferedReader(socket)
            bufferedWriter.writeString(BuildConfig.VERSION_CODE.toString())
            bufferedWriter.writeString(name)
            bufferedWriter.writeString(timeBound.toString())
            bufferedWriter.writeStringAndFlush(isPublic.toString())
            if (!isPublic){
                bufferedWriter.writeStringAndFlush(password!!)
            }
            if (bufferedReader.readString(3000) != BuildConfig.VERSION_CODE.toString()) {
                return false
            }
            val clientName = bufferedReader.readString(3000)
            Thread(PrepareClient(Client(clientName, socket))).start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return true
    }

    override fun interrupt() {
        isInterrupted = true
    }

    override fun close() {
        interrupt()
        verifiedClients.forEach { it.close() }
    }

    private inner class PrepareClient(private val client: Client): Runnable{
        override fun run() {
            try {
                val bufferedReader = SpecialBufferedReader(client.socket)
                val bufferedWriter = SpecialBufferedWriter(client.socket)
                if (bufferedReader.readString() == "start"){
                    if (!isInterrupted) clientJoinedLiveData.postValue(client)
                    interrupt()
                }
                bufferedWriter.writeStringAndFlush("start")
            }catch (e: IOException){
                e.printStackTrace()
            }
        }

    }
}