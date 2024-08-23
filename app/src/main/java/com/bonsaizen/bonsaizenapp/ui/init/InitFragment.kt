package com.bonsaizen.bonsaizenapp.ui.init

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bonsaizen.bonsaizenapp.R
import com.bonsaizen.bonsaizenapp.databinding.FragmentInitBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InitFragment : Fragment() {

    private lateinit var binding: FragmentInitBinding
    private val viewModel: InitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInitBinding.inflate(inflater, container, false)
        setOnClickListeners()
        observeViewModel()
        return binding.root
    }

    private fun setOnClickListeners() {
        binding.btnInit.setOnClickListener {
            viewModel.onStartButtonClicked()
        }

        binding.btnInit2.setOnClickListener {
            viewModel.onSignInButtonClicked()
        }
    }

    private fun observeViewModel() {

        lifecycleScope.launch {
            viewModel.navigationEvent.collect { event ->
                when (event) {
                    is NavigationEvent.NavigateToRegisterScreen -> navigateToRegisterScreen()
                    is NavigationEvent.NavigateToAuthScreen -> navigateToAuthScreen()
                }
            }
        }
    }

    private fun navigateToRegisterScreen() {
        findNavController().navigate(R.id.action_initFragment_to_registerFragment)
    }
    private fun navigateToAuthScreen() {

    }

}