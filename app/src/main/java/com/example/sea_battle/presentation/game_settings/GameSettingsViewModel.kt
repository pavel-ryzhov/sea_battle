package com.example.sea_battle.presentation.game_settings

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.sea_battle.R
import com.example.sea_battle.extensions.StringExtensions.Companion.isInt
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameSettingsViewModel @Inject constructor() : ViewModel() {

    fun checkTimeBound(context: Context, string: String): Boolean{
        return if (!string.isInt()){
            Toast.makeText(context, context.resources.getString(R.string.wrong_time), Toast.LENGTH_SHORT).show()
            false
        } else true
    }
    fun checkPassword(context: Context, string: String): Boolean{
        return if (string.isBlank()){
            Toast.makeText(context, context.resources.getString(R.string.password_is_blank), Toast.LENGTH_SHORT).show()
            false
        } else true
    }

}