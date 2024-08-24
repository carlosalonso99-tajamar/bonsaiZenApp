package com.bonsaizen.bonsaizenapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonsaizen.bonsaizenapp.domain.usecases.LoginUserUseCase
import com.bonsaizen.bonsaizenapp.domain.wrapper.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _loginUserMutableState = MutableStateFlow<Event<Boolean>?>(null)
    val loginUserMutableState: StateFlow<Event<Boolean>?> = _loginUserMutableState

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent: SharedFlow<LoginEvent> = _loginEvent

    private val _loadingMutableState = MutableStateFlow(false)
    val loadingMutableState: StateFlow<Boolean> = _loadingMutableState

    private val passwordVisibilityMutableState = MutableStateFlow(false)
    val passwordVisibilityState: StateFlow<Boolean> = passwordVisibilityMutableState


    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val res = loginUserUseCase.invoke(email, password)
            if (res.isSuccess) {
                _loginUserMutableState.value = Event(true)
                _loginState.value = LoginState.Success
            } else {
                _loginUserMutableState.value = Event(false)
                _loginState.value =
                    LoginState.Error(res.exceptionOrNull()?.message ?: "Login fallido")
                _loginEvent.emit(
                    LoginEvent.ShowErrorMessage(
                        res.exceptionOrNull()?.message ?: "Login fallido"
                    )
                )
            }
        }
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }

    sealed class LoginEvent {
        data class ShowErrorMessage(val message: String) : LoginEvent()
    }

    fun togglePasswordVisibility() {
        passwordVisibilityMutableState.value = !passwordVisibilityMutableState.value
    }
}