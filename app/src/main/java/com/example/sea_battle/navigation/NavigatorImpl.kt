package com.example.sea_battle.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.sea_battle.R
import javax.inject.Singleton

@Singleton
class NavigatorImpl(context: Context) : Navigator {
    private val supportFragmentManager = (context as AppCompatActivity).supportFragmentManager
    companion object{
        private val onBackPressedActions: MutableMap<Class<*>, Pair<Boolean, Runnable>> = mutableMapOf()
    }

    override fun openFragment(fragment: Fragment, addToBackStack: Boolean, inAnimation: Boolean) {
        supportFragmentManager.commit {
            if (inAnimation) {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
            }else{
                setCustomAnimations(
                    R.anim.slide_out,
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.slide_in
                )
            }
            replace(R.id.fragmentContainer, fragment, fragment::class.java.toString())
            if (addToBackStack) addToBackStack(fragment::class.java.toString())
        }
    }

    override fun openFragment(fragment: Fragment, args: Bundle, addToBackStack: Boolean, inAnimation: Boolean) {
        openFragment(fragment.apply { arguments = args }, addToBackStack, inAnimation)
    }

    override fun doOnBackPressed(defaultAction: Runnable) {
        getVisibleFragment()?.let {
            for (entry in onBackPressedActions) {
                if (entry.key == it::class.java) {
                    entry.value.apply {
                        second.run()
                        if (first) defaultAction.run()
                    }
                    break
                }
            }
        } ?: defaultAction.run()
    }

    override fun setOnBackPressed(clazz: Class<*>, doBackPress: Boolean, action: Runnable) {
        onBackPressedActions[clazz] = Pair(doBackPress, action)
    }

    override fun getVisibleFragment(): Fragment? {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible) return fragment
        }
        return null
    }
}