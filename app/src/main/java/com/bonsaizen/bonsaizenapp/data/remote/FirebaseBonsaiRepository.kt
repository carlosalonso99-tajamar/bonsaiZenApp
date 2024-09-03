package com.bonsaizen.bonsaizenapp.data.remote

import android.net.Uri
import android.util.Log
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
            val userId = auth.currentUser?.email ?: throw Exception("Usuario no autenticado")
            Log.d(
                "BonsaiRepository",
                "Fetching bonsais for user: $userId"
            ) // Log para el inicio de la operación

            // Referencia a la colección de bonsáis del usuario
            val bonsaiCollectionRef = firestore.collection("Users")
                .document(userId)
                .collection("bonsais")

            // Obtener los documentos de la subcolección de bonsáis
            val snapshot = bonsaiCollectionRef.get().await()

            // Convertir los documentos en objetos Bonsai
            val bonsaiList = snapshot.toObjects(Bonsai::class.java)

            Log.d("BonsaiRepository", "Fetched bonsais count: ${bonsaiList.size}") // Log para éxito

            Result.success(bonsaiList)
        } catch (e: Exception) {
            Log.e("BonsaiRepository", "Error fetching bonsais", e) // Log para errores
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