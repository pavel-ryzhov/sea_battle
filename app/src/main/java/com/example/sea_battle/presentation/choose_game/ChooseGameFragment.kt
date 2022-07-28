package com.example.sea_battle.presentation.choose_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sea_battle.MainActivity
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentChooseGameBinding
import com.example.sea_battle.entities.Host
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.dialogs.InfoDialog
import com.example.sea_battle.presentation.dialogs.JoinPrivateGameDialog
import com.example.sea_battle.presentation.start_game.StartGameFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChooseGameFragment : Fragment() {

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
        navigator.setOnBackPressed(this::class.java, true) {
            viewModel.interrupt()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeOnLiveData()
        binding.recyclerView.adapter = recyclerAdapter
        binding.relativeLayoutBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        viewModel.findServers(requireArguments().getString("clientName", ""))
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            newServerDetectedLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    recyclerAdapter.addItem(it)
                }
            }
            serverIsNotAvailableLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    InfoDialog(
                        requireActivity(),
                        resources.getString(R.string.server_is_not_available)
                    ) {
                        recyclerAdapter.removeItem(it)
                        (requireActivity() as MainActivity).hideSystemUI()
                    }.show()
                }
            }
        }
    }

    private fun onRecyclerAdapterItemClick(host: Host) {
        CoroutineScope(Dispatchers.Main + job).launch(Dispatchers.Main) {
            if (!host.isPublic) {
                JoinPrivateGameDialog(requireContext(), navigator, viewModel, host).show()
            } else {
                CoroutineScope(Dispatchers.IO + job).launch(Dispatchers.IO) {
                    if (viewModel.notifyClientJoinedGame(host)) {
                        navigator.openFragment(
                            StartGameFragment().also { it.setHost(host) },
                            Bundle().apply { putInt("timeBound", host.timeBound) }
                        )
                        viewModel.interrupt()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.interrupt()
        viewModel.notifyFragmentDestroyed()
        job.cancel()
        super.onDestroy()
    }
}