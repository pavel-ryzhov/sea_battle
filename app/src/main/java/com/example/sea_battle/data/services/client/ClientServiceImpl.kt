package com.example.sea_battle.data.services.client

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.BuildConfig
import com.example.sea_battle.entities.Host
import com.example.sea_battle.extensions.ListExtensions.Companion.containsAddress
import com.example.sea_battle.utils.SpecialBufferedReader
import com.example.sea_battle.utils.SpecialBufferedWriter
import java.io.IOException
import java.net.*
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientServiceImpl @Inject constructor() : ClientService() {

    companion object {
        private const val START_PORT = 5000
        fun getCurrentIp(): InetAddress? {
            try {
                val networkInterfaces: Enumeration<NetworkInterface> =
                    NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val ni: NetworkInterface = networkInterfaces.nextElement()
                    val nias: Enumeration<InetAddress> = ni.inetAddresses
                    while (nias.hasMoreElements()) {
                        val ia: InetAddress = nias.nextElement()
                        if (!ia.isLinkLocalAddress
                            && !ia.isLoopbackAddress
                            && ia is Inet4Address
                        ) {
                            return ia
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return null
        }

    }

    override fun notifyClientJoinedGame(host: Host): Boolean {
        return try {
            val bufferedWriter = SpecialBufferedWriter(host.socket)
            val bufferedReader = SpecialBufferedReader(host.socket)
            bufferedWriter.writeStringAndFlush("start")
            if (bufferedReader.readString(3000) == "start") {
                isJoinedToServer = true
                true
            } else {
                serverIsNotAvailableLiveData.postValue(host)
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            serverIsNotAvailableLiveData.postValue(host)
            false
        }
    }

    override val serverIsNotAvailableLiveData = MutableLiveData<Host?>()
    override val newServerDetectedLiveData = MutableLiveData<Host?>()

    private val reachableAddresses: ArrayBlockingQueue<InetAddress> = ArrayBlockingQueue(10)
    private val hosts = mutableListOf<Host>()
    private var isInterrupted = false
    private var isJoinedToServer = false
    private lateinit var executorService: ThreadPoolExecutor
    private lateinit var address: String


    override fun findServers(clientName: String, nThreads: Int, nPorts: Int) {
        hosts.clear()
        isInterrupted = false
        val localhost: String = getCurrentIp()?.hostAddress ?: return
        address = localhost.substring(0, localhost.lastIndexOf('.'))

        executorService = Executors.newFixedThreadPool(nThreads) as ThreadPoolExecutor

        checkReachableAddresses()

        Thread {
            while (!isInterrupted) {
                if (executorService.activeCount == 0) {
                    checkReachableAddresses()
                }
                Thread.sleep(3000)
            }
        }.start()

        while (!isInterrupted) {
            val inetAddress = reachableAddresses.take()
            Thread {
                var portIsSelected = false
                var startPort = START_PORT

                while (!portIsSelected && startPort < START_PORT + nPorts * 5 && !isInterrupted) {
                    val futures: ArrayList<Future<Socket?>> = ArrayList()
                    for (i in 0 until nPorts) {
                        Log.d("ssss", "adding future...")
                        futures.add(executorService.submit<Socket?> {
                            try {
                                val socket = Socket()
                                socket.connect(InetSocketAddress(inetAddress, startPort + i), 5000)
                                return@submit if (verifyServer(clientName, socket)) {
                                    socket
                                } else {
                                    socket.close()
                                    null
                                }
                            } catch (e: IOException) {
                                return@submit null
                            }
                        })
                    }
                    for (future in futures) {
                        future.get()?.let {
                            portIsSelected = true
                        }
                    }
                    startPort += nPorts
                }
            }.start()
        }
    }

    private fun checkReachableAddresses() {
        for (i in 0..255) {
            executorService.submit(IsAddressReachable(i))
        }
    }

    override fun verifyServer(clientName: String, socket: Socket): Boolean {
        try {
            val bufferedReader = SpecialBufferedReader(socket)
            if (bufferedReader.readString(3000) != BuildConfig.VERSION_CODE.toString()) {
                return false
            }
            val name = bufferedReader.readString(3000)
            val timeBound = bufferedReader.readString(3000).toInt()
            val isPublic = bufferedReader.readString(3000).toBoolean()
            var password: String? = null
            if (!isPublic) {
                password = bufferedReader.readString(3000)
            }
            val bufferedWriter = SpecialBufferedWriter(socket)
            bufferedWriter.writeString(BuildConfig.VERSION_CODE.toString())
            bufferedWriter.writeStringAndFlush(clientName)
            if (!isInterrupted) {
                val host = Host(name, timeBound, isPublic, password, socket)

                synchronized(hosts) {
                    hosts.add(host)
                }
                newServerDetectedLiveData.postValue(host)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun interrupt() {
        isInterrupted = true
    }

    override fun close() {
        interrupt()
        hosts.forEach { it.socket.close() }
        isJoinedToServer = false
    }

    override fun isJoinedToServer() = isJoinedToServer

    override fun getAllDetectedServers(): List<Host> {
        synchronized(hosts) {
            return hosts.toList()
        }
    }

    private inner class IsAddressReachable(private val addr: Int) : Runnable {
        override fun run() {
            InetAddress.getByName("$address.$addr").let {
                if (it.isReachable(3000) && !hosts.containsAddress(it)) {
                    if (!isInterrupted) reachableAddresses.put(it)
                }
            }
        }
    }
}