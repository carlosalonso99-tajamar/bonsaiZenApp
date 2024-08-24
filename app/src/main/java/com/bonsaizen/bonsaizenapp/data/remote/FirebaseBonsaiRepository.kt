package com.bonsaizen.bonsaizenapp.data.remote

import android.net.Uri
import com.bonsaizen.bonsaizenapp.data.model.bonsais.Bonsai
import com.bonsaizen.bonsaizenapp.data.repository.BonsaiRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseBonsaiRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : BonsaiRepository {

    override suspend fun addBonsai(bonsai: Bonsai): Result<Unit> {
        return try {
            val userId = auth.currentUser?.email ?: throw Exception("Usuario no autencificado")
            val bonsaiCollection = firestore.collection("Users")
                .document(userId)
                .collection("bonsais")
                .document(bonsai.name)
            val bonsaiWithId = bonsai.copy(id = bonsai.id)
            bonsaiCollection.set(bonsaiWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBonsaiList(userId: String): Result<List<Bonsai>> {
        return try {
            val querySnapshot = firestore.collection("Users")
                .document(userId)
                .collection("bonsais")
                .get()
                .await()
            val bonsaiList = querySnapshot.documents.mapNotNull { it.toObject(Bonsai::class.java) }
            Result.success(bonsaiList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun uploadImage(imageUri: Uri): Result<String> {
        return try {
            val storageRef = storage.reference.child("bonsais_images/${imageUri.lastPathSegment}")
            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}