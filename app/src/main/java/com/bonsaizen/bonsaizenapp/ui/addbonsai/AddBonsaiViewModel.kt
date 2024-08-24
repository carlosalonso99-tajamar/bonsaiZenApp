package com.bonsaizen.bonsaizenapp.ui.addbonsai

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.domain.usecases.AddBonsaiUseCase
import com.bonsaizen.bonsaizenapp.domain.usecases.UploadBonsaiImageUseCase
import com.bonsaizen.bonsaizenapp.domain.wrapper.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBonsaiViewModel @Inject constructor(
    private val addBonsaiUseCase: AddBonsaiUseCase,
    private val uploadBonsaiImageUseCase: UploadBonsaiImageUseCase
) : ViewModel() {

    private val _bonsaiState = MutableStateFlow<BonsaiState>(BonsaiState.Idle)
    val bonsaiState: StateFlow<BonsaiState> = _bonsaiState

    private val _addBonsaiMutableState = MutableStateFlow<Event<Boolean>?>(null)
    val addBonsaiMutableState: StateFlow<Event<Boolean>?> = _addBonsaiMutableState

    private val _loadingMutableState = MutableStateFlow(false)
    val loadingMutableState: StateFlow<Boolean> = _loadingMutableState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun addBonsai(bonsai: Bonsai) {
        viewModelScope.launch {
            val res = addBonsaiUseCase.execute(bonsai)
            if (res.isSuccess) {
                _addBonsaiMutableState.value = Event(true)
                _bonsaiState.value = BonsaiState.Success("Bonsai agregado correctamente")
            } else {
                _addBonsaiMutableState.value = Event(false)
                _bonsaiState.value = BonsaiState.Error(
                    res.exceptionOrNull()?.message ?: "Error al agregar el bonsai"
                )
            }
        }
    }

    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            val res = uploadBonsaiImageUseCase.invoke(imageUri)
            if (res.isSuccess) {
                _bonsaiState.value = BonsaiState.ImageUploaded(res.getOrNull())
            } else {
                _bonsaiState.value =
                    BonsaiState.Error(res.exceptionOrNull()?.message ?: "Error al subir la imagen")
            }
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