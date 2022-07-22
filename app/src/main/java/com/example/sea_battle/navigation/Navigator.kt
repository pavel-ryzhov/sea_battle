package com.example.sea_battle.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment

interface Navigator {
    fun openFragment(fragment: Fragment, addToBackStack: Boolean = true, inAnimation: Boolean = true)
    fun openFragment(fragment: Fragment, args: Bundle, addToBackStack: Boolean = true, inAnimation: Boolean = true)
    fun doOnBackPressed(defaultAction: Runnable)
    fun setOnBackPressed(clazz: Class<*>, doBackPress: Boolean, action: Runnable)
    fun getVisibleFragment(): Fragment?
}