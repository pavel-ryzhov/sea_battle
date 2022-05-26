package com.example.sea_battle.entities

import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.Socket

class SpecialBufferedWriter(private val socket: Socket) {
    private val bufferedWriter: BufferedWriter =
        BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

    fun writeString(string: String) {
        bufferedWriter.write("$string###")
    }

    fun flush() {
        bufferedWriter.flush()
    }

    fun writeStringAndFlush(string: String) {
        writeString(string)
        flush()
    }
}