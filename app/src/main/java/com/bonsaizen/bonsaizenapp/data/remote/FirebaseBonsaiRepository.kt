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
            val userId = auth.currentUser?.email ?: throw Exception("Usuario no autenticado")
            val bonsaiId = bonsai.id.ifEmpty { firestore.collection("Users").document().id }

            val bonsaiCollection = firestore.collection("Users")
                .document(userId)
                .collection("bonsais")
                .document(bonsaiId)
            val bonsaiWithId = bonsai.copy(id = bonsaiId)
            bonsaiCollection.set(bonsaiWithId).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("BonsaiRepository", "Error al agregar el bonsái", e)
            Result.failure(e)
        }
    }

    override suspend fun getBonsaiList(userId: String): Result<List<Bonsai>> {
        return try {
            val userId = auth.currentUser?.email ?: throw Exception("Usuario no autenticado")
            Log.d(
                "BonsaiRepository",
                "Fetching bonsais for user: $userId"
            )

            val bonsaiCollectionRef = firestore.collection("Users")
                .document(userId)
                .collection("bonsais")
            val snapshot = bonsaiCollectionRef.get().await()
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

    override suspend fun deleteBonsai(bonsai: Bonsai): Result<Unit> {
        return try {
            val userId = auth.currentUser?.email ?: throw Exception("Usuario no autenticado")

            Log.d("BonsaiRepository", "Eliminando bonsái con ID: ${bonsai.id}")

            val bonsaiCollection = firestore.collection("Users")
                .document(userId)
                .collection("bonsais")
                .document(bonsai.id)
            bonsaiCollection.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("BonsaiRepository", "Error al eliminar el bonsái", e)
            Result.failure(e)
        }
    }

    override suspend fun updateBonsai(bonsai: Bonsai): Result<Unit> {
        return try {
            val userId = auth.currentUser?.email ?: throw Exception("Usuario no autenticado")
            val bonsaiCollection = firestore.collection("Users")
                .document(userId)
                .collection("bonsais")
                .document(bonsai.id)
            bonsaiCollection.set(bonsai).await()
            Log.d("BonsaiRepository", "Bonsái actualizado correctamente: ${bonsai.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("BonsaiRepository", "Error al actualizar el otrosai", e) // Añadir logs
            Result.failure(e)
        }
    }

    override suspend fun updateBonsaiImage(bonsai: Bonsai, imageUri: Uri): Result<Unit> {
        return try {
            val userId = auth.currentUser?.email ?: throw Exception("Usuario no autenticado")

            // Subir la nueva imagen a Firebase Storage
            val storageRef = storage.reference.child("bonsais_images/${imageUri.lastPathSegment}")
            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

            // Obtener la referencia del bonsái en Firestore
            val bonsaiCollection = firestore.collection("Users")
                .document(userId)
                .collection("bonsais")
                .document(bonsai.id)

            // Recuperar la lista actual de imágenes del bonsái
            val currentBonsai = bonsaiCollection.get().await().toObject(Bonsai::class.java)
            val updatedImageList = currentBonsai?.images?.toMutableList() ?: mutableListOf()

            // Añadir la nueva URL de imagen a la lista
            updatedImageList.add(downloadUrl)

            // Actualizar el campo 'images' del bonsái en Firestore con la lista actualizada
            bonsaiCollection.update("images", updatedImageList).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}