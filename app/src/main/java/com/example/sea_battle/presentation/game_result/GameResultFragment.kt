package com.example.sea_battle.presentation.game_result

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.sea_battle.MainActivity
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentGameResultBinding
import com.example.sea_battle.entities.Ship
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.start_game.StartGameFragment
import com.example.sea_battle.views.PlaygroundView.Companion.THIS_PLAYER_VICTORY
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GameResultFragment : Fragment() {

    @Inject
    lateinit var navigator: Navigator
    private lateinit var binding: FragmentGameResultBinding
    private lateinit var arg: Pair<Int, List<Ship>>
    private val viewModel: GameResultViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameResultBinding.inflate(inflater, container, false)
        navigator.setOnBackPressed(this::class.java, true) {
            viewModel.postExit()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            textViewResult.text =
                getString(if (arg.first == THIS_PLAYER_VICTORY) R.string.you_have_won else R.string.you_have_lost)
            textViewResult.setTextColor(requireContext().getColor(if (arg.first == THIS_PLAYER_VICTORY) android.R.color.holo_green_light else android.R.color.holo_red_dark))
            shipsLocationView.init(arg.second)
            buttonExit.setOnClickListener {
                requireActivity().onBackPressed()
            }
            buttonPlayAgain.setOnClickListener {
                viewModel.notifyThisPlayerWannaPlayAgain()
            }
        }
        subscribeOnLiveData()
    }

    private fun subscribeOnLiveData() {
        viewModel.apply {
            otherPlayerExitedAfterGameResultLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    onOtherPlayerExited()
                }
            }
            connectionErrorLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    onOtherPlayerExited()
                }
            }
            anotherPlayerWannaPlayAgainLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.another_player_want_to_play_again),
                        Snackbar.LENGTH_LONG
                    ).apply {
                        this.view.layoutParams =
                            (this.view.layoutParams as FrameLayout.LayoutParams).apply {
                                gravity = Gravity.TOP.or(Gravity.CENTER_HORIZONTAL)
                                width = FrameLayout.LayoutParams.MATCH_PARENT
                            }
                        setAction(getString(R.string.play)) {
                            viewModel.notifyThisPlayerWannaPlayAgain()
                        }
                        show()
                    }
                }
            }
            playAgainLiveData.observe(viewLifecycleOwner) {
                it?.let {
                    navigator.openFragment(StartGameFragment(), requireArguments(), this@GameResultFragment::class.java)
                }
            }
        }
    }

    private fun onOtherPlayerExited() {
        Snackbar.make(
            binding.root,
            getString(R.string.another_player_exited),
            Snackbar.LENGTH_LONG
        ).apply {
            this.view.layoutParams =
                (this.view.layoutParams as FrameLayout.LayoutParams).apply {
                    gravity = Gravity.TOP.or(Gravity.CENTER_HORIZONTAL)
                    width = FrameLayout.LayoutParams.MATCH_PARENT
                }
            show()
        }
        binding.buttonPlayAgain.isEnabled = false
    }

    fun init(arg: Pair<Int, List<Ship>>) {
        this.arg = arg
    }

    override fun onDestroy() {
        viewModel.notifyFragmentDestroyed()
        super.onDestroy()
    }
}