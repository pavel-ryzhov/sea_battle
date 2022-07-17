package com.example.sea_battle.utils

import com.example.sea_battle.entities.Task
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.locks.ReentrantLock

class SpecialBufferedWriter(private val socket: Socket) {
    companion object {
        private val reentrantLock = ReentrantLock()
    }

    private val bufferedWriter: BufferedWriter =
        BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

    fun writeString(string: String) {
        reentrantLock.lock()
        bufferedWriter.write("$string###")
        reentrantLock.unlock()
    }

    private fun flush() {
        reentrantLock.lock()
        try {
            bufferedWriter.flush()
        } catch (e: SocketException) {
            throw SocketIsNotConnectedException()
        }
        reentrantLock.unlock()
    }

    fun writeStringAndFlush(string: String) {
        writeString(string)
        flush()
    }

    private fun writeBytes(bytes: ByteArray) {
        socket.getOutputStream().apply {
            write(bytes)
            write("###".toByteArray())
        }
    }

    fun writeTask(task: Task) {
        reentrantLock.lock()
        bufferedWriter.write("${task.tag}\n")
        bufferedWriter.flush()
        writeBytes(task.data)
        Thread.sleep(250)
        reentrantLock.unlock()
        println(task)
    }
}