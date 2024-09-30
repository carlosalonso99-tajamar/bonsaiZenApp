package com.bonsaizen.bonsaizenapp.ui.editbonsai

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.domain.usecases.UpdateBonsaiImageUseCase
import com.bonsaizen.bonsaizenapp.domain.usecases.UpdateBonsaiUseCase
import com.bonsaizen.bonsaizenapp.domain.wrapper.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBonsaiViewModel @Inject constructor(
    private val updateBonsaiUseCase: UpdateBonsaiUseCase,
    private val updateBonsaiImageUseCase: UpdateBonsaiImageUseCase
) : ViewModel() {

    private val _bonsaiState = MutableStateFlow<BonsaiState>(BonsaiState.Idle)
    val bonsaiState: StateFlow<BonsaiState> = _bonsaiState

    private val _updateBonsaiMutableState = MutableStateFlow<Event<Boolean>?>(null)
    val updateBonsaiMutableState: StateFlow<Event<Boolean>?> = _updateBonsaiMutableState

    private val _loadingMutableState = MutableStateFlow(false)
    val loadingMutableState: StateFlow<Boolean> = _loadingMutableState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun updateBonsai(bonsai: Bonsai) {
        viewModelScope.launch {
            _loadingMutableState.value = true
            val updateResult = updateBonsaiUseCase.execute(bonsai)
            if (updateResult.isSuccess) {
                _updateBonsaiMutableState.value = Event(true)
                _bonsaiState.value = BonsaiState.Success("Bonsai actualizado correctamente")
            } else {
                _updateBonsaiMutableState.value = Event(false)
                _bonsaiState.value = BonsaiState.Error(
                    updateResult.exceptionOrNull()?.message ?: "Error al actualizar el bonsai"
                )
            }
            _loadingMutableState.value = false
        }
    }

    fun updateBonsaiImage(bonsai: Bonsai, imageUri: Uri) {
        viewModelScope.launch {
            _loadingMutableState.value = true
            val updateResult = updateBonsaiImageUseCase.invoke(bonsai, imageUri)
            if (updateResult.isSuccess) {
                _bonsaiState.value = BonsaiState.ImageUploaded(updateResult.getOrNull().toString())
            } else {
                _bonsaiState.value = BonsaiState.Error(
                    updateResult.exceptionOrNull()?.message ?: "Error al actualizar la imagen"
                )
            }
            _loadingMutableState.value = false
        }
    }


    sealed class BonsaiState {
        object Idle : BonsaiState()
        object Loading : BonsaiState()
        data class Success(val message: String) : BonsaiState()
        data class Error(val message: String) : BonsaiState()
        data class ImageUploaded(val imageUrl: String?) : BonsaiState()
    }

}