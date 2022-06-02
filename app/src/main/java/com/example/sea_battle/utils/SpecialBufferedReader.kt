package com.example.sea_battle.utils

import com.example.sea_battle.entities.Task
import com.example.sea_battle.extensions.ListExtensions.Companion.byteListEndsWithString
import com.example.sea_battle.extensions.ListExtensions.Companion.endsWithString
import com.example.sea_battle.extensions.ListExtensions.Companion.removeGrids
import com.example.sea_battle.extensions.StringExtensions.Companion.removeGrids
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.net.SocketTimeoutException

class SpecialBufferedReader(private val socket: Socket) {
    private val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    fun readString(timeout: Int = 0) : String{
        try{
            val chars = mutableListOf<Char>()
            while (!chars.endsWithString("###")) {
                socket.soTimeout = timeout
                chars.add(bufferedReader.read().toChar())
            }
            return String(chars.toCharArray()).removeGrids()
        }catch(e: SocketTimeoutException){
            throw SocketIsNotConnectedException()
        }
    }
    fun readTask(timeout: Int = 0): Task{
        try {
            socket.soTimeout = timeout
            return Task(bufferedReader.readLine(), readBytes(timeout))
        }catch(e: SocketTimeoutException){
            throw SocketIsNotConnectedException()
        }
    }
    fun readBytes(timeout: Int = 0) : ByteArray{
        try{
            val bytes = mutableListOf<Byte>()
            while (!bytes.byteListEndsWithString("###")) {
                socket.soTimeout = timeout
                bytes.add(socket.getInputStream().read().toByte())
            }
            return bytes.removeGrids().toByteArray()
        }catch(e: SocketTimeoutException){
            throw SocketIsNotConnectedException()
        }
    }
}