package com.example.sea_battle

import com.example.sea_battle.extensions.StringExtensions.Companion.isInt
import com.example.sea_battle.extensions.StringExtensions.Companion.isNumber
import com.example.sea_battle.extensions.StringExtensions.Companion.removeGrids
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class StringExtensionsTest {
    @Test
    fun testRemoveGrids(){
        assertEquals("frkrfkrfkrfkr\tvccdcd de\\", "###frkrfkrfkrfkr\tvccdcd de\\###".removeGrids())
        assertEquals("#frkrfkrfkrfkr\tvccdcd de\\", "####frkrfkrfkrfkr\tvccdcd de\\###".removeGrids())
        assertEquals("#frkrfkrfkrfkr\tvccdcd de\\##", "####frkrfkrfkrfkr\tvccdcd de\\#####".removeGrids())
        assertEquals("frkrfkrfkrfkr\tvccdcd de\\", "frkrfkrfkrfkr\tvccdcd de\\".removeGrids())
    }
    @Test
    fun testIsNumber(){
        assertTrue("783".isNumber())
        assertTrue("78343478438743787834834893487233290498478893232".isNumber())
        assertFalse("783434784387437878348fr34893487233290498478893232".isNumber())
        assertFalse("".isNumber())
    }
    @Test
    fun testIsInt(){
        assertTrue("783".isInt())
        assertFalse("78343478438743787834834893487233290498478893232".isInt())
        assertFalse("783434784387437878348fr34893487233290498478893232".isInt())
        assertFalse("".isInt())
    }
}