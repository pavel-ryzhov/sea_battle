package com.example.sea_battle.extensions

import com.example.sea_battle.extensions.ListExtensions.Companion.byteListEndsWithString

class ByteArrayExtensions {
    companion object {
        fun ByteArray.removeGrids(): ByteArray {
            return this.removeGridsInTheEnd().reversedArray().removeGridsInTheEnd().reversedArray()
        }
        private fun ByteArray.removeGridsInTheEnd(): ByteArray {
            val bytes = this.toMutableList()
            while (bytes.byteListEndsWithString("###")) {
                for (i in 0..2)
                    bytes.removeLast()
            }
            return bytes.toByteArray()
        }
    }
}