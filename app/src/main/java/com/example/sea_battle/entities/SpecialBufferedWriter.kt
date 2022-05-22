package com.example.sea_battle.entities

import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter

class SpecialBufferedWriter(outputStream: OutputStream) {
    private val bufferedWriter: BufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
    fun writeString(string: String){
        bufferedWriter.write("$string###")
    }
    fun flush(){
        bufferedWriter.flush()
    }
    fun writeStringAndFlush(string: String){
        writeString(string)
        flush()
    }
}