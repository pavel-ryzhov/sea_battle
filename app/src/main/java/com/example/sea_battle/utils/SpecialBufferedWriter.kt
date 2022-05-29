package com.example.sea_battle.utils

import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.Socket
import java.net.SocketException

class SpecialBufferedWriter(socket: Socket) {
    private val bufferedWriter: BufferedWriter =
        BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

    fun writeString(string: String) {
        bufferedWriter.write("$string###")
    }

    fun flush() {
        try {
            bufferedWriter.flush()
        }catch (e: SocketException){
            throw SocketIsNotConnectedException()
        }
    }

    fun writeStringAndFlush(string: String) {
        writeString(string)
        flush()
    }

    fun writeBytes(bytes: ByteArray){
        writeString(String(bytes))
    }

    fun writeBytesAndFlush(bytes: ByteArray){
        writeStringAndFlush(String(bytes))
    }
}