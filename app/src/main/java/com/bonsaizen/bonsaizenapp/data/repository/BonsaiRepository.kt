package com.bonsaizen.bonsaizenapp.data.repository

import android.net.Uri
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai

interface BonsaiRepository {

    suspend fun addBonsai(bonsai: Bonsai): Result<Unit>
    suspend fun getBonsaiList(userId: String): Result<List<Bonsai>>
    suspend fun uploadImage(imageUri: Uri): Result<String>
    suspend fun deleteBonsai(bonsai: Bonsai): Result<Unit>

}