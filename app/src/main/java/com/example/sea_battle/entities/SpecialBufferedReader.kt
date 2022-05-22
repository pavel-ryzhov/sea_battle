package com.example.sea_battle.entities

import com.example.sea_battle.extensions.ListExtensions.Companion.endsWithString
import com.example.sea_battle.extensions.StringExtensions.Companion.removeGrids
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class SpecialBufferedReader(inputStream: InputStream) {
    private val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    fun readString() : String{
        val chars = mutableListOf<Char>()
        while (!chars.endsWithString("###")){
            chars.add(bufferedReader.read().toChar())
        }
        return String(chars.toCharArray()).removeGrids()
    }
    fun readBytes(): ByteArray{
        return readString().toByteArray()
    }
}