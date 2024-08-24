package com.bonsaizen.bonsaizenapp.ui.bonsais

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.domain.usecases.GetBonsaiUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class BonsaiListViewModel @Inject constructor(
    private val getBonsaisUseCase: GetBonsaiUseCase
) : ViewModel() {

    private val _bonsaiList = MutableStateFlow<List<Bonsai>>(emptyList())
    val bonsaiList: StateFlow<List<Bonsai>> = _bonsaiList

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getBonsaiList(userId: String) {
        viewModelScope.launch {
            val result = getBonsaisUseCase.invoke(userId)
            if (result.isSuccess) {
                _bonsaiList.value = result.getOrDefault(emptyList())
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
        }
    }

}