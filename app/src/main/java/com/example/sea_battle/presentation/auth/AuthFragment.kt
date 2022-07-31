package com.example.sea_battle.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.sea_battle.MainActivity
import com.example.sea_battle.R
import com.example.sea_battle.databinding.FragmentAuthBinding
import com.example.sea_battle.navigation.Navigator
import com.example.sea_battle.presentation.general.GeneralFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment(){
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentAuthBinding
    @Inject
    lateinit var navigator: Navigator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        navigator.setOnBackPressed(this::class.java, false){
            requireActivity().apply {
                if (supportFragmentManager.backStackEntryCount <= 1){
                    finish()
                }else{
                    (this as MainActivity).onBackPressedAppCompatActivity()
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonNext.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val checkNameResult = viewModel.checkName(requireContext(), name)
            if (checkNameResult == null){
                navigator.openFragment(GeneralFragment(), Bundle().apply { putString("name", name) })
            }else{
                binding.textInputLayoutName.apply {
                    error = checkNameResult
                    startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.error))
                }
            }
        }
        binding.relativeLayoutBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.editTextName.addTextChangedListener {
            val name = binding.editTextName.text.toString()
            val checkNameResult = viewModel.checkNameWithoutSaving(requireContext(), name)
            if (checkNameResult == null){
                binding.textInputLayoutName.error = null
            }
        }
    }

    override fun onResume() {
        (requireActivity() as MainActivity).hideSystemUI()
        super.onResume()
    }
}