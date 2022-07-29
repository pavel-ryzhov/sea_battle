package com.example.sea_battle.presentation.playground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.sea_battle.MainActivity
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentPlaygroundBinding
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.dialogs.ConfirmActionDialog
import com.example.sea_battle.presentation.dialogs.InfoDialog
import com.example.sea_battle.presentation.game_result.GameResultFragment
import com.example.sea_battle.presentation.start_game.StartGameFragment
import dagger.hilt.android.AndroidEntryPoint
import java.net.Socket
import javax.inject.Inject

@AndroidEntryPoint
class PlaygroundFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var binding: FragmentPlaygroundBinding
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
        navigator.setOnBackPressed(this::class.java, false) {
            requireActivity().supportFragmentManager.apply {
                ConfirmActionDialog(requireActivity(),
                    getString(R.string.do_you_really_want_to_exit),
                    onConfirmed = {
                        viewModel.postExit()
                        (requireActivity() as MainActivity).onBackPressedAppCompatActivity()
                    }).show()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeOnLiveData()
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            bothPlayersAreReadyLiveData.observe(viewLifecycleOwner) {
                it?.let {
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
            gameFinishedLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    navigator.openFragment(
                        GameResultFragment().apply { init(it) },
                        requireArguments().apply {
                            putBoolean("gameInited", true)
                        },
                        this@PlaygroundFragment::class.java
                    )
                }
            }
            otherPlayerExitedLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    InfoDialog(
                        requireActivity(),
                        getString(R.string.another_player_left_the_game)
                    ) {
                        (requireActivity() as MainActivity).onBackPressedAppCompatActivity()
                    }.show()
                }
            }
            connectionErrorLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    InfoDialog(requireActivity(), getString(R.string.connection_error)) {
                        (requireActivity() as MainActivity).onBackPressedAppCompatActivity()
                    }.show()
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.notifyFragmentDestroyed()
        super.onDestroy()
    }

    override fun onStop() {
        viewModel.gameService.clickLiveData.postValue(null)
        super.onStop()
    }
}