package com.example.sea_battle

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
}