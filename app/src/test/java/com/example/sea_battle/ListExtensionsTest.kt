package com.example.sea_battle

import com.example.sea_battle.extensions.ListExtensions.Companion.byteListEndsWithString
import com.example.sea_battle.extensions.ListExtensions.Companion.endsWithString
import org.junit.Assert.*
import org.junit.Test

class ListExtensionsTest {
    @Test
    fun testEndsWithString(){
        val chars: List<Char> = listOf('3', '8', 'l', 'd', 'z', '0', '-', 'k')

        assertFalse(chars.endsWithString("eroid"))
        assertFalse(chars.endsWithString("38"))

        assertTrue(chars.endsWithString("-k"))
        assertTrue(chars.endsWithString("z0-k"))
        assertTrue(chars.endsWithString(""))
    }
    @Test
    fun testByteListEndsWithString(){
        val chars: List<Byte> = listOf('3'.code.toByte(), '8'.code.toByte(), 'l'.code.toByte(), 'd'.code.toByte(), 'е'.code.toByte(), '0'.code.toByte(), '-'.code.toByte(), 'л'.code.toByte())

        assertFalse(chars.byteListEndsWithString("eroid"))
        assertFalse(chars.byteListEndsWithString("38"))

        assertTrue(chars.byteListEndsWithString("-л"))
        assertTrue(chars.byteListEndsWithString("е0-л"))
        assertTrue(chars.byteListEndsWithString(""))
    }
}