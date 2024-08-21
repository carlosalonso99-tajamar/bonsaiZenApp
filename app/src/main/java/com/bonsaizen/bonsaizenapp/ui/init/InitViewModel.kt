package com.bonsaizen.bonsaizenapp.ui.init


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _isUserAuthenticated = MutableStateFlow(auth.currentUser != null)
    val isUserAuthenticated: StateFlow<Boolean> = _isUserAuthenticated

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    fun onStartButtonClicked() {
        if (auth.currentUser != null) {
            viewModelScope.launch {
                _navigationEvent.emit(NavigationEvent.NavigateToMainScreen)
            }
        } else {
            viewModelScope.launch {
                _navigationEvent.emit(NavigationEvent.NavigateToAuthScreen)
            }
        }
    }

    fun onSignInButtonClicked() {
        if (auth.currentUser != null) {
            viewModelScope.launch {
                _navigationEvent.emit(NavigationEvent.NavigateToMainScreen)
            }
        } else {
            viewModelScope.launch {
                _navigationEvent.emit(NavigationEvent.NavigateToAuthScreen)
            }
        }
    }
}

sealed class NavigationEvent {
    object NavigateToMainScreen : NavigationEvent()
    object NavigateToAuthScreen : NavigationEvent()
}