package com.example.sea_battle

import com.example.sea_battle.utils.SpecialBufferedReader
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream

class SpecialBufferedReaderTest {
    @Test
    fun testReadString(){
        val byteArrayInputStream = ByteArrayInputStream("eedorj\n\neieorire###oortw rorwoigt###oktr".toByteArray())
        val specialBufferedReader = SpecialBufferedReader(byteArrayInputStream)

        assertEquals("eedorj\n\neieorire", specialBufferedReader.readString())
        assertEquals("oortw rorwoigt", specialBufferedReader.readString())
        assertEquals("oktr", specialBufferedReader.readString())
        assertEquals("", specialBufferedReader.readString())
    }
}