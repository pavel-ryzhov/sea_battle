package com.example.sea_battle.presentation.choose_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.sea_battle.databinding.FragmentChooseGameBinding
import com.example.sea_battle.entities.Host
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Socket

@AndroidEntryPoint
class ChooseGameFragment : Fragment(){

    private lateinit var binding: FragmentChooseGameBinding
    private val viewModel: ChooseGameViewModel by viewModels()
    private val recyclerAdapter = ServersRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeOnLiveData()
        binding.recyclerView.adapter = recyclerAdapter
        binding.relativeLayoutBack.setOnClickListener {
            viewModel.interrupt()
            requireActivity().onBackPressed()
        }
        viewModel.findServers(requireArguments().getString("clientName", ""))
    }
    private fun subscribeOnLiveData(){
        viewModel.apply {
            newServerDetectedLiveData.observe(viewLifecycleOwner){
                recyclerAdapter.addItem(it)
            }
            netScanned.observe(viewLifecycleOwner){

            }
        }
    }

    override fun onStop() {
        viewModel.interrupt()
        super.onStop()
    }
}