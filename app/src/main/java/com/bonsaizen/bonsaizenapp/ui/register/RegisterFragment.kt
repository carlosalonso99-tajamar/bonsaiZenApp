package com.bonsaizen.bonsaizenapp.ui.register

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bonsaizen.bonsaizenapp.R
import com.bonsaizen.bonsaizenapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupUI(binding.root)
        setupListeners()
        observeViewModel()
        setOnClickListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is RegisterViewModel.RegisterState.Idle -> {
                        Log.d("RegisterFragment", "Idle")
                    }

                    is RegisterViewModel.RegisterState.Loading -> {
                        Log.d("RegisterFragment", "Loading")
                    }

                    is RegisterViewModel.RegisterState.Success -> {

                        Log.d("RegisterFragment", "Success")

                    }
                    is RegisterViewModel.RegisterState.Error -> {
                        Log.d("RegisterFragment", "Error")
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.registerUserMutableState.collect { event ->
                event?.getContentIfNotHandled()?.let { isRegistered ->
                    if (isRegistered) {
                        Log.d("RegisterFragment", "User registered")
                        showAlertDialog(
                            getString(R.string.user_register),
                            getString(R.string.user_success)
                        ) {

                            if (isAdded && findNavController().currentDestination?.id == R.id.registerFragment) {
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.loadingMutableState.collect { isLoading ->
                showProgressBar(isLoading)
            }
        }

        lifecycleScope.launch {
            viewModel.registerEvent.collect { event ->
                when (event) {
                    is RegisterViewModel.RegisterEvent.ShowErrorMessage -> {
                        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.passwordVisibilityState.collect { isVisible ->
                binding.etPassword.transformationMethod =
                    if (isVisible) HideReturnsTransformationMethod.getInstance()
                    else PasswordTransformationMethod.getInstance()
                binding.etPassword.setSelection(binding.etPassword.text.length)
            }
        }

        lifecycleScope.launch {
            viewModel.passwordRepeatVisibilityState.collect { isVisible ->
                binding.etRepeatPassword.transformationMethod =
                    if (isVisible) HideReturnsTransformationMethod.getInstance()
                    else PasswordTransformationMethod.getInstance()
                binding.etRepeatPassword.setSelection(binding.etRepeatPassword.text.length)
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnRegister.setOnClickListener {
            val email = binding.etMail.text.toString()
            val password = binding.etPassword.text.toString()
            val repeatPassword = binding.etRepeatPassword.text.toString()

            val isEmailEmpty = email.isEmpty()
            val isPasswordEmpty = password.isEmpty()
            val isRepeatPasswordEmpty = repeatPassword.isEmpty()

            updateEditTextUI(binding.etMail, isEmailEmpty)
            updateEditTextUI(binding.etPassword, isPasswordEmpty)
            updateEditTextUI(binding.etRepeatPassword, isRepeatPasswordEmpty)

            when{
                isEmailEmpty || isPasswordEmpty || isRepeatPasswordEmpty  -> {
                    showAlertDialog(
                        getString(R.string.error),
                        getString(R.string.user_register_error)
                    )
                }
                !validatePasswords() -> {
                    updatePasswordEditTextUI(true)
                    showAlertDialog(
                        getString(R.string.error),
                        getString(R.string.error_password_mismatch)
                    )
                }
                else -> {
                    showProgressBar(true)
                    viewModel.registerUser(email, password)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.etPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etPassword.right - binding.etPassword.compoundDrawables[2].bounds.width())) {
                    viewModel.togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.etRepeatPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etRepeatPassword.right - binding.etRepeatPassword.compoundDrawables[2].bounds.width())) {
                    viewModel.togglePasswordRepeatVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun updateEditTextUI(editText: EditText, isEmpty: Boolean) {
        if (isEmpty) {
            editText.setHintTextColor(Color.RED)
            editText.setTextColor(Color.BLACK)
            editText.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_background_error)
        } else {
            editText.setHintTextColor(Color.GRAY)
            editText.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button)
        }
    }

    private fun updatePasswordEditTextUI(isMismatch: Boolean) {
        if (isMismatch) {
            binding.etPassword.setHintTextColor(Color.RED)
            binding.etPassword.setTextColor(Color.BLACK)
            binding.etPassword.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_background_error)
            binding.etRepeatPassword.setHintTextColor(Color.RED)
            binding.etRepeatPassword.setTextColor(Color.BLACK)
            binding.etRepeatPassword.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_background_error)
        } else {
            binding.etPassword.setHintTextColor(Color.GRAY)
            binding.etPassword.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button)
            binding.etRepeatPassword.setHintTextColor(Color.GRAY)
            binding.etRepeatPassword.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button)
        }
    }

    private fun showAlertDialog(
        title: String,
        message: String,
        onDismiss: (() -> Unit)? = null
    ) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
                onDismiss?.invoke()
            }
        }, 2500)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard()
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun validatePasswords(): Boolean {
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etRepeatPassword.text.toString()
        return password == confirmPassword
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}



