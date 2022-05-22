package com.example.sea_battle.extensions

import java.math.BigInteger

class StringExtensions {
    companion object{
        fun String.removeGrids(): String{
            return removeFirstGrids().reversed().removeFirstGrids().reversed()
        }
        private fun String.removeFirstGrids(): String{
            var str = this
            while (str.startsWith("###")){
                str = str.replaceFirst("###", "")
            }
            return str
        }
        fun String.isNumber(): Boolean{
            return matches(Regex("\\d+"))
        }
        fun String.isInt(): Boolean{
            return isNumber() && BigInteger(this).compareTo(BigInteger(Int.MAX_VALUE.toString())) == -1
        }
    }
}