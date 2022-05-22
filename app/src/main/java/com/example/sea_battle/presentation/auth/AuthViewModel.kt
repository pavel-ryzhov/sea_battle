package com.example.sea_battle.presentation.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sea_battle.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val appPreferences: AppPreferences) : ViewModel() {
    val nameIsCorrectLiveData = MutableLiveData<String>()
    val nameIsWrongLiveData = MutableLiveData<String>()
    fun checkName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!name.matches(Regex("[a-zA-Zа-яА-Я0-9_]+"))) {
                nameIsWrongLiveData.postValue("Неправильное имя!")
            } else {
                nameIsCorrectLiveData.postValue(name)
                appPreferences.saveName(name)
            }
        }
    }
}