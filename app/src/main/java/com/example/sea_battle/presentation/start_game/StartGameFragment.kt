package com.example.sea_battle.presentation.start_game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.sea_battle.MainActivity
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentStartGameBinding
import com.example.sea_battle.entities.Client
import com.example.sea_battle.entities.Host
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.dialogs.ConfirmActionDialog
import com.example.sea_battle.presentation.dialogs.InfoDialog
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
    private var gameInited = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartGameBinding.inflate(inflater, container, false)
        navigator.setOnBackPressed(this::class.java, false) {
            requireActivity().supportFragmentManager.apply {
                if (viewModel.isOtherPlayerJoined(requireArguments().getBoolean("host", false))) {
                    ConfirmActionDialog(requireActivity(),
                        getString(R.string.do_you_really_want_to_exit),
                        onConfirmed = {
                            viewModel.postExit()
                            navigator.popBackStack(StartGameFragment::class.java)
                            (requireActivity() as MainActivity).onBackPressedAppCompatActivity()
                        }).show()
                } else {
                    viewModel.close()
                    navigator.popBackStack(StartGameFragment::class.java)
                    (requireActivity() as MainActivity).onBackPressedAppCompatActivity()
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as MainActivity).hideSystemUI()
        subscribeOnLiveData()
        requireArguments().let {
            gameInited = it.getBoolean("gameInited", false)
            if (it.getBoolean("host", false)) {
                isHost = true
                binding.textView.text =
                    resources.getString(R.string.wait_until_another_player_joins)
                if (!gameInited)
                    viewModel.startServer(
                        it.getString("name", ""),
                        it.getInt("timeBound"),
                        it.getBoolean("isPublic", true),
                        it.getString("password")
                    )
            }
        }
        setEnabled(gameInited)

        binding.apply {
            buttonBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            buttonReady.setOnClickListener {
                viewModel.checkIfUserIsReady(binding.field.ships)
            }
        }

        if (gameInited) {
            viewModel.restart()
        }

        if (!isHost && !gameInited) {
            start()
        }
    }

    private fun start() {
        setEnabled(true)
        viewModel.startListening(if (!isHost) this@StartGameFragment.host.socket else client.socket)
    }

    fun setHost(host: Host) {
        this.host = host
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            clientJoinedLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    if (!gameInited) {
                        client = it
                        start()
                        Log.d("mmmm", "clientJoinedLiveData")
                        viewModel.notifyClientJoined(it)
                    }
                }
            }
            userIsReadyLiveData.observe(viewLifecycleOwner) {
                if (it) {
                    navigator.openFragment(PlaygroundFragment().apply {
                    }, requireArguments().apply {
                        if (!gameInited) {
                            putString(
                                "otherPlayerName",
                                if (!isHost) this@StartGameFragment.host.name else client.name
                            )
                            putInt(
                                "timeBound",
                                if (!isHost) host.timeBound else requireArguments().getInt("timeBound")
                            )
                        }
                    })
                    if (!gameInited)
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
            otherPlayerExitedLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    InfoDialog(
                        requireActivity(),
                        getString(R.string.another_player_left_the_game)
                    ) {
                        requireActivity().onBackPressed()
                    }.show()
                }
            }
            connectionErrorLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    InfoDialog(requireActivity(), getString(R.string.connection_error)) {
                        requireActivity().onBackPressed()
                    }.show()
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

    override fun onDestroy() {
        if (!gameInited)
            viewModel.interrupt()
        viewModel.notifyFragmentDestroyed()
        super.onDestroy()
    }
}