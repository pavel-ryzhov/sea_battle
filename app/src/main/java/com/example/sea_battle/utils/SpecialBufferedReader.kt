package com.example.sea_battle.utils

import com.example.sea_battle.entities.Task
import com.example.sea_battle.extensions.ListExtensions.Companion.byteListEndsWithString
import com.example.sea_battle.extensions.ListExtensions.Companion.removeGrids
import com.example.sea_battle.extensions.StringExtensions.Companion.removeGrids
import java.net.Socket
import java.net.SocketTimeoutException

class SpecialBufferedReader(private val socket: Socket) {
    private val inputStream = socket.getInputStream()

    fun readString(timeout: Int = 0): String {
        try {
            val bytes = mutableListOf<Byte>()
            while (!bytes.byteListEndsWithString("###")) {
                socket.soTimeout = timeout
                bytes.add(inputStream.read().toByte())
            }
            return String(bytes.toByteArray()).removeGrids()
        } catch (e: SocketTimeoutException) {
            throw SocketIsNotConnectedException()
        }
    }

    private fun readLine(timeout: Int = 0): String {
        try {
            socket.soTimeout = timeout
            val bytes = mutableListOf<Byte>()
            do {
                val b = inputStream.read().toByte()
                if (b == (-1).toByte())
                    throw SocketIsNotConnectedException()
                else
                    bytes.add(b)
            } while (bytes.last() != '\n'.code.toByte())
            bytes.removeLast()
            return String(bytes.toByteArray())
        } catch (e: SocketTimeoutException) {
            throw SocketIsNotConnectedException()
        }
    }

    fun readTask(timeout: Int = 0): Task {
        try {
            return Task(readLine(timeout), readBytes(timeout))
        } catch (e: SocketTimeoutException) {
            throw SocketIsNotConnectedException()
        }
    }

    fun readBytes(timeout: Int = 0): ByteArray {
        try {
            socket.soTimeout = timeout
            val bytes = mutableListOf<Byte>()
            while (!bytes.byteListEndsWithString("###")) {
                bytes.add(inputStream.read().toByte())
            }
            return bytes.removeGrids().toByteArray()
        } catch (e: SocketTimeoutException) {
            throw SocketIsNotConnectedException()
        }
    }
}