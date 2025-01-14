package org.chewing.v1.implementation.user.user

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Component

@Component
class UserValidator(
    val userRepository: UserRepository,
) {
    fun isAccess(user: User) {
        if (user.status != AccessStatus.ACCESS) {
            throw ConflictException(ErrorCode.USER_NOT_ACCESS)
        }
    }

    fun isNotAlreadyCreated(credential: Credential) {
        userRepository.readByCredential(credential)?.let {
            if (it.status == AccessStatus.ACCESS) {
                throw ConflictException(ErrorCode.USER_ALREADY_CREATED)
            }
        }
    }

    fun isAlreadyCreated(credential: Credential) {
        val user = userRepository.readByCredential(credential)
        if (user == null || user.status != AccessStatus.ACCESS) {
            throw ConflictException(ErrorCode.USER_NOT_CREATED)
        }
    }
}
