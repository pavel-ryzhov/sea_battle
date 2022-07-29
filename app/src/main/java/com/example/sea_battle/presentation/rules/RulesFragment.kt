package com.example.sea_battle.presentation.rules

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentRulesBinding
import com.example.sea_battle.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RulesFragment : Fragment(){

    @Inject
    lateinit var navigator: Navigator
    private lateinit var binding: FragmentRulesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRulesBinding.inflate(inflater, container, false)
        navigator.setOnBackPressed(this::class.java, true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.relativeLayoutBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.textViewRulesLink.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.rules_link))))
        }
    }
}