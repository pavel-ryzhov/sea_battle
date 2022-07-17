package com.example.sea_battle.extensions

class ListExtensions {
    companion object{
        fun List<Char>.endsWithString(string: String): Boolean{
            if (size < string.length) return false
            for (i in string.indices){
                if (get(size - string.length + i) != string[i]){
                    return false
                }
            }
            return true
        }
        fun List<Byte>.byteListEndsWithString(string: String): Boolean{
            val strArray = string.toByteArray()
            if (size < strArray.size) return false
            for (i in strArray.indices){
                if (get(size - strArray.size + i) != strArray[i]){
                    return false
                }
            }
            return true
        }
        fun List<Byte>.removeGrids(): List<Byte> {
            return this.removeGridsInTheEnd().reversed().removeGridsInTheEnd().reversed()
        }
        private fun List<Byte>.removeGridsInTheEnd(): List<Byte> {
            val bytes = this.toMutableList()
            while (bytes.byteListEndsWithString("###")) {
                for (i in 0..2)
                    bytes.removeLast()
            }
            return bytes
        }
    }
}