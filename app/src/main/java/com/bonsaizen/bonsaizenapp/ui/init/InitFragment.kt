package com.bonsaizen.bonsaizenapp.ui.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bonsaizen.bonsaizenapp.R
import com.bonsaizen.bonsaizenapp.databinding.FragmentInitBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InitFragment : Fragment() {

    private lateinit var binding: FragmentInitBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInitBinding.inflate(inflater, container, false)
        setOnClickListeners()

        return binding.root
    }

    private fun setOnClickListeners() {
        binding.btnInit.setOnClickListener {
            findNavController().navigate(R.id.action_initFragment_to_registerFragment)
        }

        binding.btnInit2.setOnClickListener {
            findNavController().navigate(R.id.action_initFragment_to_loginFragment)
        }
    }


}