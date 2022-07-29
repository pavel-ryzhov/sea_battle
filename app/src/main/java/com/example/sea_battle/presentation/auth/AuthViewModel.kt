package com.example.sea_battle.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.sea_battle.R
import com.example.sea_battle.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val appPreferences: AppPreferences) : ViewModel() {

    fun checkName(context: Context, name: String): String? {
        val result = checkNameWithoutSaving(context, name)
        if (result == null) {
            appPreferences.saveName(name)
        }
        return result
    }
    fun checkNameWithoutSaving(context: Context, name: String) = when {
        name.isBlank() -> context.getString(R.string.name_is_blank)
        !name.matches(Regex("[a-zA-Zа-яА-Я0-9_]+")) -> context.getString(R.string.name_can_contain_only_letters_numbers_and_underscores)
        else -> null
    }
}