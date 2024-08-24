package com.bonsaizen.bonsaizenapp.ui.bonsais

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bonsaizen.bonsaizenapp.R
import com.bonsaizen.bonsaizenapp.databinding.FragmentBonsaiListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BonsaiListFragment : Fragment() {

    private lateinit var binding: FragmentBonsaiListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBonsaiListBinding.inflate(inflater, container, false)
        setOnClickListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setOnClickListeners() {
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_bonsaiListFragment_to_addBonsaiFragment)
        }

        binding.ivExit.setOnClickListener {
            showExitConfirmationDialog()
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_exit_title))
            .setMessage(getString(R.string.confirm_exit_message))
            .setPositiveButton(getString(R.string.confirm)) { dialog, which ->

                findNavController().navigate(R.id.action_bonsaiListFragment_to_initFragment)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->

                dialog.dismiss()
            }
            .create()
            .show()
    }
}

