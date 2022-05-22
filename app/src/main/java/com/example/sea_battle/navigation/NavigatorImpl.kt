package com.example.sea_battle.navigation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.sea_battle.R

class NavigatorImpl (private val context: Context) : Navigator{
    override fun openFragment(fragment: Fragment, addToBackStack: Boolean) {
        (context as AppCompatActivity).supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fragmentContainer, fragment)
            if (addToBackStack) addToBackStack(null)
        }
    }

    override fun openFragment(fragment: Fragment, args: Bundle, addToBackStack: Boolean) {
        openFragment(fragment.apply { arguments = args }, addToBackStack)
    }
}