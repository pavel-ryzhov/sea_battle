package com.example.sea_battle.data.preferences

import android.content.Context

class AppPreferencesImpl (context: Context) : AppPreferences {
    private val sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    override fun saveName(name: String) {
        sharedPreferences.edit().putString("name", name).apply()
    }

    override fun getName(): String? {
        return sharedPreferences.getString("name", null)
    }
}