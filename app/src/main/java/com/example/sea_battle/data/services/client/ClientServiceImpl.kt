package com.example.sea_battle.data.services.client

import androidx.lifecycle.MutableLiveData
import com.example.sea_battle.BuildConfig
import com.example.sea_battle.entities.Host
import com.example.sea_battle.utils.SocketIsNotConnectedException
import com.example.sea_battle.utils.SpecialBufferedReader
import com.example.sea_battle.utils.SpecialBufferedWriter
import java.io.IOException
import java.net.*
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientServiceImpl @Inject constructor() : ClientService() {

    companion object{
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

    override fun notifyClientJoinedGame(host: Host): Boolean{
        return try {
            val bufferedWriter = SpecialBufferedWriter(host.socket)
            val bufferedReader = SpecialBufferedReader(host.socket)
            bufferedWriter.writeStringAndFlush("start")
            bufferedReader.readString(1000) == "start"
        }catch (e: SocketIsNotConnectedException){
            e.printStackTrace()
            serverIsNotAvailableLiveData.postValue(host)
            false
        }
    }

    override val serverIsNotAvailableLiveData = MutableLiveData<Host>()
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
            val bufferedReader = SpecialBufferedReader(socket)
            if (bufferedReader.readString(3000) != BuildConfig.VERSION_CODE.toString()) {
                return false
            }
            val name = bufferedReader.readString(3000)
            val timeBound = bufferedReader.readString(3000).toInt()
            val isPublic = bufferedReader.readString(3000).toBoolean()
            var password: String? = null
            if (!isPublic){
                password = bufferedReader.readString(3000)
            }
            val bufferedWriter = SpecialBufferedWriter(socket)
            bufferedWriter.writeString(BuildConfig.VERSION_CODE.toString())
            bufferedWriter.writeStringAndFlush(clientName)
            if (!isInterrupted) newServerDetectedLiveData.postValue(Host(name, timeBound, isPublic, password, socket))
        }catch (e: IOException){
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