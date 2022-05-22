package com.example.sea_battle.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment

interface Navigator {
    fun openFragment(fragment: Fragment, addToBackStack: Boolean)
    fun openFragment(fragment: Fragment, args: Bundle, addToBackStack: Boolean)
}