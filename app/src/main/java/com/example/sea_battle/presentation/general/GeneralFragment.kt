package com.example.sea_battle.presentation.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sea_battle.presentation.auth.AuthFragment
import com.example.sea_battle.databinding.FragmentGeneralBinding
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.choose_game.ChooseGameFragment
import com.example.sea_battle.presentation.game_settings.GameSettingsFragment
import com.example.sea_battle.presentation.start_game.StartGameFragment
import com.example.sea_battle.presentation.rules.RulesFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GeneralFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var binding: FragmentGeneralBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGeneralBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.editTextName.setText(requireArguments().getString("name"))
        binding.buttonChangeName.setOnClickListener {
            navigator.openFragment(AuthFragment(), true)
        }
        binding.buttonRules.setOnClickListener {
            navigator.openFragment(RulesFragment(), true)
        }
        binding.buttonCreateGame.setOnClickListener {
            navigator.openFragment(GameSettingsFragment(), Bundle().apply {
                putBoolean("host", true)
                putString("name", requireArguments().getString("name"))
            }, true)
        }
        binding.buttonJoinGame.setOnClickListener {
            navigator.openFragment(
                ChooseGameFragment(),
                Bundle().apply { putString("clientName", requireArguments().getString("name")) },
                true
            )
        }
    }
}