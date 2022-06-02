package com.example.sea_battle.presentation.playground

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sea_battle.databinding.FragmentPlaygroundBinding
import com.example.sea_battle.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import java.net.Socket
import javax.inject.Inject

@AndroidEntryPoint
class PlaygroundFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var binding: FragmentPlaygroundBinding
    lateinit var otherPlayerSocket: Socket
    private val viewModel: PlaygroundViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaygroundBinding.inflate(inflater, container, false)
        binding.playground.init(
            requireArguments().getString("otherPlayerName", ""),
            requireArguments().getInt("timeBound"),
            viewModel.gameService
        )
        binding.playground.isEnabled = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        subscribeOnLiveData()
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            bothPlayersAreReadyLiveData.observe(viewLifecycleOwner) {
                binding.apply {
                    constraintLayoutLoading.visibility = View.GONE
                    playground.apply {
                        setFirstTurn(it)
                        start()
                        isEnabled = true
                    }
                }
            }
        }
    }
}