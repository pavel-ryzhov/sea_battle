package com.example.sea_battle

import com.example.sea_battle.utils.SpecialBufferedWriter
import org.junit.Test
import org.junit.Assert.assertEquals
import java.io.ByteArrayOutputStream

class SpecialBufferedWriterTest {
    @Test
    fun testWriteString(){
        val byteArrayOutputStream = ByteArrayOutputStream()

        val specialBufferedWriter = SpecialBufferedWriter(byteArrayOutputStream)

        specialBufferedWriter.writeString("dedeede frrffr rrffr\n rfrfrfrfr")
        specialBufferedWriter.writeString("deedik\nn\n rrfrfrfrf")
        specialBufferedWriter.flush()

        assertEquals("dedeede frrffr rrffr\n rfrfrfrfr###deedik\nn\n rrfrfrfrf###", byteArrayOutputStream.toString())
    }
}