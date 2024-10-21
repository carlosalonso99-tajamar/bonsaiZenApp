package com.bonsaizen.bonsaizenapp.domain.usecases

import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.data.repository.BonsaiRepository
import javax.inject.Inject

class AddBonsaiUseCase @Inject constructor(
    private val repository: BonsaiRepository
) {
    suspend fun execute(bonsai: Bonsai): Result<Unit> {
        return repository.addBonsai(bonsai)
    }
}