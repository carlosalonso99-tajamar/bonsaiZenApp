package com.bonsaizen.bonsaizenapp.domain.usecases

import com.bonsaizen.bonsaizenapp.data.model.users.User
import com.bonsaizen.bonsaizenapp.data.repository.AuthRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.registerUser(email, password)
    }
}