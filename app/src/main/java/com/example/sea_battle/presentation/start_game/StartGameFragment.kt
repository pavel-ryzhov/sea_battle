package com.example.sea_battle.presentation.start_game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sea_battle.MainActivity
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentStartGameBinding
import com.example.sea_battle.entities.Client
import com.example.sea_battle.entities.Host
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.playground.PlaygroundFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartGameFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var binding: FragmentStartGameBinding
    private lateinit var host: Host
    private lateinit var client: Client
    private val viewModel: StartGameViewModel by viewModels()
    private var isHost = false
    private var needToCloseSocketOnExit = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        needToCloseSocketOnExit = true
        binding = FragmentStartGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as MainActivity).hideSystemUI()
        subscribeOnLiveData()
        requireArguments().let {
            if (it.getBoolean("host", false)) {
                isHost = true
                binding.textView.text =
                    resources.getString(R.string.wait_until_another_player_joins)
                viewModel.startServer(
                    it.getString("name", ""),
                    it.getInt("timeBound"),
                    it.getBoolean("isPublic", true),
                    it.getString("password")
                )
            }
        }
        setEnabled(false)

        binding.apply {
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            buttonReady.setOnClickListener {
                viewModel.checkIfUserIsReady(binding.field.ships)
            }
        }

        if (!isHost) {
            start()
        }
    }

    private fun start() {
        setEnabled(true)
    }

    fun setHost(host: Host) {
        this.host = host
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            clientJoinedLiveData.observe(viewLifecycleOwner) {
                client = it
                start()
            }
            userIsReadyLiveData.observe(viewLifecycleOwner) {
                if (it) {
                    needToCloseSocketOnExit = false
                    navigator.openFragment(PlaygroundFragment().apply {
                        otherPlayerSocket =
                            if (!isHost) this@StartGameFragment.host.socket else client.socket
                        otherPlayerName =
                            if (!isHost) this@StartGameFragment.host.name else client.name
                    }, Bundle().apply {
                        putInt("timeBound", requireArguments().getInt("timeBound"))
                    }, true)
                    viewModel.setOtherPlayer(if (!isHost) this@StartGameFragment.host.socket else client.socket)
                    viewModel.notifyThisPlayerIsReadyToStart(binding.field.ships)
                } else {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.not_all_ships_are_arranged),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setEnabled(enabled: Boolean) {
        binding.apply {
            field.isEnabled = enabled
            buttonReady.isEnabled = enabled
            constraintLayoutLoading.visibility = if (enabled) View.GONE else View.VISIBLE
        }
    }

    override fun onStop() {
        if (needToCloseSocketOnExit) viewModel.close()
        super.onStop()
    }
}