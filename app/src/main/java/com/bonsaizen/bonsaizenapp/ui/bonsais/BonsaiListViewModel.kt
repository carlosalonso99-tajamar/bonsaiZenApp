package com.bonsaizen.bonsaizenapp.ui.bonsais

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.domain.usecases.DeleteBonsaiUseCase
import com.bonsaizen.bonsaizenapp.domain.usecases.GetBonsaiUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BonsaiListViewModel @Inject constructor(
    private val getBonsaisUseCase: GetBonsaiUseCase,
    private val deleteBonsaiUseCase: DeleteBonsaiUseCase
) : ViewModel() {

    private val _bonsaiList = MutableStateFlow<List<Bonsai>>(emptyList())
    val bonsaiList: StateFlow<List<Bonsai>> = _bonsaiList

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun getBonsaiList() {
        viewModelScope.launch {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            _loading.value = true
            Log.d("BonsaiListViewModel", "Fetching bonsai list...")
            val result = getBonsaisUseCase.invoke(userID ?: "")
            if (result.isSuccess) {
                _bonsaiList.value = result.getOrDefault(emptyList())
                Log.d(
                    "BonsaiListViewModel",
                    "Bonsai list fetched successfully, count: ${_bonsaiList.value.size}"
                )
            } else {
                _error.value = result.exceptionOrNull()?.message
                Log.e("BonsaiListViewModel", "Error fetching bonsai list: ${_error.value}")
            }
            _loading.value = false
        }
    }

    fun deleteBonsai(bonsai: String) {
        viewModelScope.launch {
            FirebaseAuth.getInstance().currentUser?.uid
            _loading.value = true
            Log.d("BonsaiListViewModel", "Deleting bonsai: $bonsai")
            val result = deleteBonsaiUseCase.execute(Bonsai(name = bonsai))
            if (result.isSuccess) {
                _bonsaiList.value = _bonsaiList.value.filter { it.name != bonsai }
                Log.d("BonsaiListViewModel", "Bonsai deleted successfully")
            } else {
                _error.value = result.exceptionOrNull()?.message
                Log.e("BonsaiListViewModel", "Error deleting bonsai: ${_error.value}")
            }
            _loading.value = false
        }
    }
}
