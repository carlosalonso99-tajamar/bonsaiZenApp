package com.bonsaizen.bonsaizenapp.domain.usecases

import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.data.repository.BonsaiRepository
import javax.inject.Inject

class GetBonsaiUseCase @Inject constructor(
    private val repository: BonsaiRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Bonsai>> {
        return repository.getBonsaiList(userId)
    }

}