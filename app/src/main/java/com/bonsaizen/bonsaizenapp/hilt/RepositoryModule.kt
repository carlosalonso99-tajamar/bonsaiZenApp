package com.bonsaizen.bonsaizenapp.hilt

import com.bonsaizen.bonsaizenapp.data.remote.FireBaseAuthRepository
import com.bonsaizen.bonsaizenapp.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return FireBaseAuthRepository(auth, firestore)
    }
}