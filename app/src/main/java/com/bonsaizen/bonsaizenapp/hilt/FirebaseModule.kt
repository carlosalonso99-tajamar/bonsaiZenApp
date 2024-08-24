package com.bonsaizen.bonsaizenapp.hilt

import com.bonsaizen.bonsaizenapp.data.remote.FirebaseBonsaiRepository
import com.bonsaizen.bonsaizenapp.data.repository.BonsaiRepository
import com.bonsaizen.bonsaizenapp.domain.usecases.AddBonsaiUseCase
import com.bonsaizen.bonsaizenapp.domain.usecases.GetBonsaiUseCase
import com.bonsaizen.bonsaizenapp.domain.usecases.UploadBonsaiImageUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideBonsaiRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): BonsaiRepository {
        return FirebaseBonsaiRepository(firestore, storage, auth = FirebaseAuth.getInstance())
    }

    @Provides
    @Singleton
    fun provideSaveBonsaiUseCase(repository: BonsaiRepository): AddBonsaiUseCase {
        return AddBonsaiUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetBonsaiListUseCase(repository: BonsaiRepository): GetBonsaiUseCase {
        return GetBonsaiUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUploadBonsaiImageUseCase(repository: BonsaiRepository): UploadBonsaiImageUseCase {
        return UploadBonsaiImageUseCase(repository)
    }


}