package com.example.sea_battle.utils

import com.example.sea_battle.entities.Task
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.Socket
import java.net.SocketException

class SpecialBufferedWriter(private val socket: Socket) {
    private val bufferedWriter: BufferedWriter =
        BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

    fun writeString(string: String) {
        synchronized(socket) {
            bufferedWriter.write("$string###")
        }
    }

    fun flush() {
        synchronized(socket) {
            try {
                bufferedWriter.flush()
            } catch (e: SocketException) {
                throw SocketIsNotConnectedException()
            }
        }
    }

    fun writeStringAndFlush(string: String) {
        writeString(string)
        flush()
    }

    fun writeBytes(bytes: ByteArray){
        synchronized(socket) {
            socket.getOutputStream().apply {
                write(bytes)
                write("###".toByteArray())
            }
        }
    }

    fun writeTask(task: Task){
        synchronized(socket) {
            bufferedWriter.write(task.tag + "\n")
            bufferedWriter.flush()
        }
        writeBytes(task.data)
    }
}