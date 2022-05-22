package com.example.sea_battle.presentation.game_settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentGameSettingsBinding
import com.example.sea_battle.extensions.StringExtensions.Companion.isInt
import com.example.sea_battle.extensions.StringExtensions.Companion.isNumber
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.start_game.StartGameFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GameSettingsFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var binding: FragmentGameSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.buttonNext.setOnClickListener {
            val timeBound = binding.editTextTimeBound.text.toString()
            if (!timeBound.isInt()){
                Toast.makeText(requireContext(), resources.getString(R.string.wrong_time), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            navigator.openFragment(StartGameFragment(), Bundle().apply {
                requireArguments().let {
                    putBoolean("host", it.getBoolean("host"))
                    putString("name", it.getString("name"))
                    putInt("timeBound", timeBound.toInt())
                }
            }, true)
        }
    }
}