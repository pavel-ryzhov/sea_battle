package com.example.sea_battle.entities

data class Task(val tag: String, val data: ByteArray){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (tag != other.tag) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}