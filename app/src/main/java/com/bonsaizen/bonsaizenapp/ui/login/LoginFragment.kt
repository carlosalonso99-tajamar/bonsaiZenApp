package com.bonsaizen.bonsaizenapp.ui.login

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
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bonsaizen.bonsaizenapp.R
import com.bonsaizen.bonsaizenapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupUI(binding.root)
        setOnClickListeners()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginViewModel.LoginState.Idle -> {
                        Log.d("LoginFragment", "Idle")
                    }

                    is LoginViewModel.LoginState.Loading -> {
                        Log.d("LoginFragment", "Loading")
                    }

                    is LoginViewModel.LoginState.Success -> {
                        Log.d("LoginFragment", "Success")
                    }

                    is LoginViewModel.LoginState.Error -> {
                        Log.d("LoginFragment", "Error")
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.loginUserMutableState.collect { event ->
                event?.getContentIfNotHandled()?.let { isLoggedIn ->
                    if (isLoggedIn) {
                        Log.d("LoginFragment", "User logged in")
                        findNavController().navigate(R.id.action_loginFragment_to_bonsaiListFragment)
                    } else {
                        Log.d("LoginFragment", "User not logged in")
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
            viewModel.loginEvent.collect { event ->
                when (event) {
                    is LoginViewModel.LoginEvent.ShowErrorMessage -> {
                        showProgressBar(false)
                        showAlertDialog(
                            getString(R.string.error),
                            getString(R.string.credentials)
                        )
                        Log.d("LoginFragment", "Error message: ${event.message}")
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
    }

    private fun setOnClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etMail.text.toString()
            val password = binding.etPassword.text.toString()

            val isEmailEmpty = email.isEmpty()
            val isPasswordEmpty = password.isEmpty()

            updateEditTextUI(binding.etMail, isEmailEmpty)
            updateEditTextUI(binding.etPassword, isPasswordEmpty)

            when {
                isEmailEmpty || isPasswordEmpty -> {
                    showAlertDialog(
                        getString(R.string.error),
                        getString(R.string.user_register_error)
                    )
                }

                else -> {
                    showProgressBar(true)
                    viewModel.loginUser(email, password)
                }
            }

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
                setupUI(view.getChildAt(i))
            }
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

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

}