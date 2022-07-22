package com.example.sea_battle.presentation.game_settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentGameSettingsBinding
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.start_game.StartGameFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GameSettingsFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var binding: FragmentGameSettingsBinding
    private val viewModel: GameSettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameSettingsBinding.inflate(inflater, container, false)
        navigator.setOnBackPressed(this::class.java, true){}
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.buttonNext.setOnClickListener {
            val timeBound = binding.editTextTimeBound.text.toString()
            val password = binding.editTextPassword.text.toString()
            val isPublic = binding.radioButtonTypePublic.isChecked
            if (!viewModel.checkTimeBound(
                    requireContext(),
                    timeBound
                ) || (!isPublic && !viewModel.checkPassword(
                    requireContext(),
                    password
                ))
            ) {
                return@setOnClickListener
            }
            navigator.openFragment(StartGameFragment(), Bundle().apply {
                requireArguments().let {
                    putBoolean("host", it.getBoolean("host"))
                    putString("name", it.getString("name"))
                    putInt("timeBound", timeBound.toInt())
                    putBoolean("isPublic", isPublic)
                    putString("password", if (isPublic) null else password)
                }
            })
        }
        binding.radioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.radioButtonTypePrivate -> {
                    binding.textInputLayoutPassword.visibility = View.VISIBLE
                }
                R.id.radioButtonTypePublic -> {
                    binding.textInputLayoutPassword.visibility = View.GONE
                }
            }
        }
    }
}