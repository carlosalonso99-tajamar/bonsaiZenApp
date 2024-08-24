package com.bonsaizen.bonsaizenapp.domain.usecases

import android.net.Uri
import com.bonsaizen.bonsaizenapp.data.repository.BonsaiRepository
import javax.inject.Inject

class UploadBonsaiImageUseCase @Inject constructor(
    private val bonsaiRepository: BonsaiRepository
) {
    suspend operator fun invoke(imageUri: Uri): Result<String> {
        return bonsaiRepository.uploadImage(imageUri)
    }

}