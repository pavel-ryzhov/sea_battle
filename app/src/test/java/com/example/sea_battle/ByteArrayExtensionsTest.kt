package com.example.sea_battle

import com.example.sea_battle.extensions.ByteArrayExtensions.Companion.removeGrids
import org.junit.Assert
import org.junit.Test

class ByteArrayExtensionsTest {
    @Test
    fun testRemoveGrids(){
        Assert.assertArrayEquals(
            "frkrfkrfkrfkr\tvccdcd de\\".toByteArray(),
            "###frkrfkrfkrfkr\tvccdcd de\\###".toByteArray().removeGrids()
        )
        Assert.assertArrayEquals(
            "#frkrfkrfkrfkr\tvccdcd de\\".toByteArray(),
            "####frkrfkrfkrfkr\tvccdcd de\\###".toByteArray().removeGrids()
        )
        Assert.assertArrayEquals(
            "#frkrfkrfkrfkr\tvccdcd de\\##".toByteArray(),
            "####frkrfkrfkrfkr\tvccdcd de\\#####".toByteArray().removeGrids()
        )
        Assert.assertArrayEquals(
            "frkrfkrfkrfkr\tvccdcd de\\".toByteArray(),
            "frkrfkrfkrfkr\tvccdcd de\\".toByteArray().removeGrids()
        )
    }
}