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
    lateinit var otherPlayerName: String
    lateinit var otherPlayerSocket: Socket
    private val viewModel: PlaygroundViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaygroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.startListening()

        subscribeOnLiveData()
    }
    private fun subscribeOnLiveData(){
        viewModel.apply {
            bothPlayersAreReadyLiveData.observe(viewLifecycleOwner){
                binding.constraintLayoutLoading.visibility = View.GONE 
            }
        }
    }
}