package com.example.sea_battle.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sea_battle.databinding.FragmentAuthBinding
import com.example.sea_battle.presentation.general.GeneralFragment
import com.example.sea_battle.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment(){
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentAuthBinding
    @Inject
    lateinit var navigator: Navigator
    private var executeLiveData: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonNext.setOnClickListener {
            executeLiveData = true
            viewModel.checkName(binding.editTextName.text.toString())
        }
        subscribeOnLiveData()
    }
    private fun subscribeOnLiveData(){
        viewModel.apply {
            nameIsCorrectLiveData.observe(viewLifecycleOwner){
                if (executeLiveData) {
                    navigator.openFragment(GeneralFragment(), Bundle().apply { putString("name", it) }, true)
                    executeLiveData = false
                }
            }
            nameIsWrongLiveData.observe(viewLifecycleOwner){
                if (executeLiveData) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    executeLiveData = false
                }
            }
        }
    }
}