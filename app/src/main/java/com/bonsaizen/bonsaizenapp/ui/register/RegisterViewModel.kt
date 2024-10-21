package com.bonsaizen.bonsaizenapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonsaizen.bonsaizenapp.domain.usecases.RegisterUserUseCase
import com.bonsaizen.bonsaizenapp.domain.wrapper.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    private val _registerUserMutableState = MutableStateFlow<Event<Boolean>?>(null)
    val registerUserMutableState: StateFlow<Event<Boolean>?> = _registerUserMutableState

    private val _registerEvent = MutableSharedFlow<RegisterEvent>()
    val registerEvent : SharedFlow<RegisterEvent> = _registerEvent

    private val _loadingMutableState = MutableStateFlow(false)
    val loadingMutableState: StateFlow<Boolean> = _loadingMutableState

    private val passwordVisibilityMutableState = MutableStateFlow(false)
    val passwordVisibilityState: StateFlow<Boolean> = passwordVisibilityMutableState

    private val passwordRepeatVisibilityMutableState = MutableStateFlow(false)
    val passwordRepeatVisibilityState: StateFlow<Boolean> = passwordRepeatVisibilityMutableState

    fun onRegisterUser() {
        // Aquí se haría la lógica de registro
        val success = true // Este es un ejemplo. Deberías usar el resultado de la lógica de registro.
        _registerUserMutableState.value = Event(success)
    }

    fun registerUser(email: String, password: String){
        viewModelScope.launch {
            val res = registerUserUseCase.invoke(email, password)
            if (res.isSuccess){
                _registerUserMutableState.value = Event(true)
                _registerState.value = RegisterState.Success
            } else {
                _registerUserMutableState.value = Event(false)
                _registerState.value = RegisterState.Error(res.exceptionOrNull()?.message ?: "Registro fallido")
                _registerEvent.emit(RegisterEvent.ShowErrorMessage(res.exceptionOrNull()?.message ?: "Registro fallido"))
            }
        }
    }

    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    sealed class RegisterEvent {
        data class ShowErrorMessage(val message: String) : RegisterEvent()
    }

    fun togglePasswordVisibility() {
        passwordVisibilityMutableState.value = !passwordVisibilityMutableState.value
    }

    fun togglePasswordRepeatVisibility() {
        passwordRepeatVisibilityMutableState.value = !passwordRepeatVisibilityMutableState.value
    }

}