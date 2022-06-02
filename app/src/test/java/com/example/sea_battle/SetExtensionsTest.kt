package com.example.sea_battle

import com.example.sea_battle.extensions.SetExtensions.Companion.containsAllIntArrays
import org.junit.Test
import org.junit.Assert.*

class SetExtensionsTest {
    @Test
    fun testContainsAllIntArrays(){
        val set = setOf(intArrayOf(32, 8734, 343), intArrayOf(9843, 43, 9), intArrayOf(7, 0, 43))
        assertTrue(set.containsAllIntArrays(setOf(intArrayOf(32, 8734, 343), intArrayOf(9843, 43, 9))))
        assertFalse(set.containsAllIntArrays(setOf(intArrayOf(32, 8734, 343), intArrayOf(9843, 43, 9), intArrayOf(8))))
    }
}