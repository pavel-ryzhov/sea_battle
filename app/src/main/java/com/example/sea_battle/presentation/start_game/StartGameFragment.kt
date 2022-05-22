package com.example.sea_battle.presentation.start_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentStartGameBinding
import com.example.sea_battle.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartGameFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var binding: FragmentStartGameBinding
    private val viewModel: StartGameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireArguments().let {
            if (it.getBoolean("host", false)) {
                binding.textView.text = resources.getString(R.string.wait_until_another_player_joins)
                viewModel.startServer(it.getString("name", ""), it.getInt("timeBound"))
                setAllEnabled(false)
            }
        }

        binding.apply {
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            buttonNext.setOnClickListener {

            }
        }


    }

    private fun subscribeOnLiveData(){
        viewModel.clientJoinedLiveData.observe(viewLifecycleOwner){

        }
    }

    private fun setAllEnabled(enabled: Boolean){
        binding.apply {
            field.isEnabled = enabled
            buttonNext.isEnabled = enabled
        }
    }
}