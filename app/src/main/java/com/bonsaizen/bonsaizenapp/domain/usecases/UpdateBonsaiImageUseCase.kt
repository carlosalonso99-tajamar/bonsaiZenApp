package com.bonsaizen.bonsaizenapp.domain.usecases

import android.net.Uri
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.data.repository.BonsaiRepository
import javax.inject.Inject

class UpdateBonsaiImageUseCase @Inject constructor(
    private val bonsaiRepository: BonsaiRepository
) {
    suspend operator fun invoke(bonsai: Bonsai, newImageUrl: Uri): Result<Unit> {
        return bonsaiRepository.updateBonsaiImage(bonsai, newImageUrl)
    }
}