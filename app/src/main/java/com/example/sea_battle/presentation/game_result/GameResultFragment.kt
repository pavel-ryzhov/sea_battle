package com.example.sea_battle.presentation.game_result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentGameResultBinding
import com.example.sea_battle.entities.Ship
import com.example.sea_battle.views.PlaygroundView.Companion.THIS_PLAYER_VICTORY

class GameResultFragment : Fragment() {
    private lateinit var viewBinding: FragmentGameResultBinding
    private lateinit var arg: Pair<Int, List<Ship>>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentGameResultBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewBinding.apply {
            textViewResult.text = getString(if (arg.first == THIS_PLAYER_VICTORY) R.string.you_have_won else R.string.you_have_lost)
            textViewResult.setTextColor(requireContext().getColor(if (arg.first == THIS_PLAYER_VICTORY) android.R.color.holo_green_light else android.R.color.holo_red_dark))
            shipsLocationView.init(arg.second)
        }
    }
    fun init(arg: Pair<Int, List<Ship>>){
        this.arg = arg
    }
}