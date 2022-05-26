package com.example.sea_battle.presentation.choose_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.sea_battle.data.services.ClientServiceImpl
import com.example.sea_battle.databinding.FragmentChooseGameBinding
import com.example.sea_battle.entities.Host
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.start_game.StartGameFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.Socket
import javax.inject.Inject

@AndroidEntryPoint
class ChooseGameFragment : Fragment(){

    @Inject
    lateinit var navigator: Navigator

    private lateinit var binding: FragmentChooseGameBinding
    private val viewModel: ChooseGameViewModel by viewModels()
    private val recyclerAdapter = ServersRecyclerAdapter(this::onRecyclerAdapterItemClick)
    private val job = Job()

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
        ClientServiceImpl.serverIsNotAvailableLiveData.observe(viewLifecycleOwner){
            Toast.makeText(context, "server is not available", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStop() {
        viewModel.interrupt()
        super.onStop()
    }

    private fun onRecyclerAdapterItemClick(host: Host){
        CoroutineScope(Dispatchers.IO + job).launch(Dispatchers.IO) {
            if (!host.isPublic){
                JoinPrivateGameDialog(requireContext(), navigator, host).show()
            }else {
                if (ClientServiceImpl.notifyClientJoinedGame(host))
                    navigator.openFragment(StartGameFragment().also { it.setHost(host) }, Bundle(), true)
                else
                    ClientServiceImpl.serverIsNotAvailableLiveData.postValue(host)
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}