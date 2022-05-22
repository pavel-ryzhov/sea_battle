package com.example.sea_battle.data.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.BuildConfig
import com.example.sea_battle.entities.Host
import com.example.sea_battle.entities.SpecialBufferedReader
import com.example.sea_battle.entities.SpecialBufferedWriter
import java.io.IOException
import java.net.*
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


class ClientServiceImpl @Inject constructor() : ClientService() {

    companion object{
        private const val START_PORT = 5000;
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

    override val newServerDetectedLiveData = MutableLiveData<Host>()
    override val netScanned = MutableLiveData<Unit>()

    private val reachableAddresses: ArrayBlockingQueue<InetAddress> = ArrayBlockingQueue(10)
    private val hosts = mutableListOf<Host>()
    private var isInterrupted = false
    private lateinit var executorService: ExecutorService
    private lateinit var address: String
    private var atomicInteger = AtomicInteger(0)


    override fun findServers(clientName: String, nThreads: Int, nPorts: Int) {
        isInterrupted = false
        val localhost: String = getCurrentIp()?.hostAddress ?: return
        address = localhost.substring(0, localhost.lastIndexOf('.'))

        executorService = Executors.newFixedThreadPool(nThreads)

        for (i in 0..255){
            executorService.submit(IsAddressReachable(i))
        }

        while (!isInterrupted){
            val inetAddress = reachableAddresses.take()
            Thread{
                var portIsSelected = false
                var startPort  = START_PORT

                while (!portIsSelected && startPort  < START_PORT + nPorts * 5 && !isInterrupted){
                    val futures: ArrayList<Future<Socket?>> = ArrayList()
                    for (i in 0 until nPorts){
                        futures.add(executorService.submit<Socket?>{
                            try {
                                val socket = Socket()
                                socket.connect(InetSocketAddress(inetAddress, startPort + i), 5000)
                                return@submit if (verifyServer(clientName, socket)) {
                                    socket
                                } else {
                                    socket.close()
                                    null
                                }
                            }catch (e: IOException){
                                return@submit null
                            }
                        })
                    }
                    for (future in futures){
                        future.get()?.let {
                            portIsSelected = true
                        }
                        if (atomicInteger.incrementAndGet() == 256 + 5 * nPorts){
                            netScanned.postValue(Unit)
                        }
                    }
                    startPort += nPorts
                }
            }.start()
        }

    }

    override fun verifyServer(clientName: String, socket: Socket): Boolean {
        try {
            val bufferedReader = SpecialBufferedReader(socket.getInputStream())
            val str = bufferedReader.readString()
            if (str != BuildConfig.VERSION_CODE.toString()) {
                return false
            }
            val name = bufferedReader.readString()
            val timeBound = bufferedReader.readString().toInt()
            val bufferedWriter = SpecialBufferedWriter(socket.getOutputStream())
            bufferedWriter.writeString(BuildConfig.VERSION_CODE.toString())
            bufferedWriter.writeString(clientName)
            if (!isInterrupted) newServerDetectedLiveData.postValue(Host(name, timeBound, socket))
        }catch (e: IOException){
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun interrupt() {
        isInterrupted = true
    }

    override fun getAllDetectedServers(): List<Host> {
        synchronized(hosts){
            return hosts.toList()
        }
    }

    private inner class IsAddressReachable(private val addr: Int): Runnable{
        override fun run() {
            InetAddress.getByName("$address.$addr").let {
                if (it.isReachable(3000)){
                    if(!isInterrupted) reachableAddresses.put(it)
                }
            }
            atomicInteger.incrementAndGet()
        }
    }
}