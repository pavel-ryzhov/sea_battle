package com.example.sea_battle.navigation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.sea_battle.R
import javax.inject.Singleton

@Singleton
class NavigatorImpl(context: Context) : Navigator {
    private val supportFragmentManager = (context as AppCompatActivity).supportFragmentManager

    companion object {
        private val onBackPressedActions: MutableMap<Class<*>, Pair<Boolean, Runnable>> =
            mutableMapOf()
    }

    override fun openFragment(fragment: Fragment, popFragment: Class<out Fragment>?) {
        popFragment?.let {
            popBackStack(it)
        }
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fragmentContainer, fragment, fragment::class.java.name)
            addToBackStack(fragment::class.java.name)
            commit()
        }
    }

    override fun openFragment(
        fragment: Fragment,
        args: Bundle,
        popFragment: Class<out Fragment>?
    ) {
        openFragment(fragment.apply { arguments = args }, popFragment)
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

    override fun popBackStack(vararg clazz: Class<out Fragment>) {
        for (i in clazz) {
            supportFragmentManager.popBackStack(i.name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun popBackStack(ids: IntRange) {
        for (i in ids) {
            supportFragmentManager.popBackStack(
                supportFragmentManager.getBackStackEntryAt(i).id,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

    override fun popBackStack(index: Int) {
        supportFragmentManager.popBackStack(
            supportFragmentManager.getBackStackEntryAt(index).id,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
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