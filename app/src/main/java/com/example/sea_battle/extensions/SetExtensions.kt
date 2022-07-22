package com.example.sea_battle.extensions

class SetExtensions {
   companion object{
       fun Set<IntArray>.containsAllIntArrays(arrays: Collection<IntArray>): Boolean{
           var a = 0
           for (i in this){
               for (j in arrays){
                   if (i.contentEquals(j)) a++
               }
           }
           return a == arrays.size
       }
       fun MutableSet<IntArray>.addIntArray(intArray: IntArray){
           if (!containsIntArray(intArray)){
               add(intArray)
           }
       }
       private fun Set<IntArray>.containsIntArray(intArray: IntArray): Boolean{
           for (i in this){
               if (intArray.contentEquals(i)) return true
           }
           return false
       }
   }
}