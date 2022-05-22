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
    }
}