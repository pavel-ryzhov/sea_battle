package com.example.sea_battle.presentation.game_settings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.sea_battle.R
import com.example.sea_battle.extensions.StringExtensions.Companion.isInt
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameSettingsViewModel @Inject constructor() : ViewModel() {

    fun checkTimeBound(context: Context, string: String): String?{
        return when{
            string.isBlank() -> context.getString(R.string.this_field_cannot_be_blank)
            !string.isInt() -> context.getString(R.string.the_time_must_be_less_than) + " " + Int.MAX_VALUE + "!"
            string.toInt() < 10 -> context.getString(R.string.minimum_value_is_10)
            else -> null
        }
    }
    fun checkPassword(context: Context, string: String): String?{
        return when{
            string.isBlank() -> context.getString(R.string.password_is_blank)
            !string.matches(Regex("[a-zA-Zа-яА-Я0-9_]+")) -> context.getString(R.string.password_can_contain_only_letters_numbers_and_underscores)
            else -> null
        }
    }

}