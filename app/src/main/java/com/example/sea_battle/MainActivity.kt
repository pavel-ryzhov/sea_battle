package com.example.sea_battle

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.sea_battle.presentation.auth.AuthFragment
import com.example.sea_battle.presentation.general.GeneralFragment
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.data.preferences.AppPreferences
import com.example.sea_battle.presentation.playground.PlaygroundFragment
import dagger.hilt.android.AndroidEntryPoint
import java.net.InetAddress
import java.net.UnknownHostException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object{
        fun isInternetAvailable(): Boolean {
            try {
                val address: InetAddress = InetAddress.getByName("www.google.com")
                return !address.equals("")
            } catch (e: UnknownHostException) {
            }
            return false
        }
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var appPreferences: AppPreferences

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val name = appPreferences.getName()

        navigator.openFragment(if (name == null) AuthFragment() else GeneralFragment())



    }
    fun onBackPressedAppCompatActivity() = super.onBackPressed()

    override fun onBackPressed() {
        navigator.doOnBackPressed(this::onBackPressedAppCompatActivity)
    }

    fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(
            window, findViewById(R.id.activityMain)
        ).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

    }

    override fun onResume() {
        hideSystemUI()
        super.onResume()
    }
}