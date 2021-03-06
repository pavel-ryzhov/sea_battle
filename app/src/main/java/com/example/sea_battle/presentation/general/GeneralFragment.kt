package com.example.sea_battle.presentation.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sea_battle.data.preferences.AppPreferences
import com.example.sea_battle.databinding.FragmentGeneralBinding
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.auth.AuthFragment
import com.example.sea_battle.presentation.choose_game.ChooseGameFragment
import com.example.sea_battle.presentation.game_settings.GameSettingsFragment
import com.example.sea_battle.presentation.rules.RulesFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeneralFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
    @Inject
    lateinit var preferences: AppPreferences
    private lateinit var binding: FragmentGeneralBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGeneralBinding.inflate(inflater, container, false)
        navigator.setOnBackPressed(this::class.java, false){
            requireActivity().finish()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.editTextName.setText(preferences.getName())
        binding.buttonChangeName.setOnClickListener {
            navigator.openFragment(AuthFragment())
        }
        binding.buttonRules.setOnClickListener {
            navigator.openFragment(RulesFragment())
        }
        binding.buttonCreateGame.setOnClickListener {
            navigator.openFragment(GameSettingsFragment(), Bundle().apply {
                putBoolean("host", true)
                putString("name", preferences.getName())
            })
        }
        binding.buttonJoinGame.setOnClickListener {
            navigator.openFragment(
                ChooseGameFragment(),
                Bundle().apply { putString("clientName", preferences.getName()) },
            )
        }
    }
}