package com.example.sea_battle.data.preferences

interface AppPreferences {
    fun saveName(name: String)
    fun getName(): String?
}