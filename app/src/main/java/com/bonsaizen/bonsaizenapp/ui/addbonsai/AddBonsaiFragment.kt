package com.bonsaizen.bonsaizenapp.ui.addbonsai

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bonsaizen.bonsaizenapp.R
import com.bonsaizen.bonsaizenapp.databinding.FragmentAddBonsaiBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBonsaiFragment : Fragment() {

    private lateinit var binding : FragmentAddBonsaiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBonsaiBinding.inflate(inflater, container, false)
        return binding.root
    }


}