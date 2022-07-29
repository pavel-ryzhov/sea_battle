package com.example.sea_battle.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment

interface Navigator {
    fun openFragment(fragment: Fragment, popFragment: Class<out Fragment>? = null)
    fun openFragment(fragment: Fragment, args: Bundle, popFragment: Class<out Fragment>? = null)
    fun doOnBackPressed(defaultAction: Runnable)
    fun setOnBackPressed(clazz: Class<*>, doBackPress: Boolean, action: Runnable = Runnable{})
    fun getVisibleFragment(): Fragment?
    fun popBackStack(vararg clazz: Class<out Fragment>)
    fun popBackStack(ids: IntRange)
    fun popBackStack(id: Int)
}