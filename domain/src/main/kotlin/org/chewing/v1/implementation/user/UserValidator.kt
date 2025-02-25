package org.chewing.v1.implementation.user

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Component

@Component
class UserValidator(
    val userRepository: UserRepository,
) {
    fun isNotAlreadyCreated(contact: Contact) {
        val user = userRepository.readByContact(contact, AccessStatus.ACCESS)
        if (user != null) {
            throw ConflictException(ErrorCode.USER_ALREADY_CREATED)
        }
    }

    fun isAlreadyCreated(contact: Contact) {
        val user = userRepository.readByContact(contact, AccessStatus.ACCESS)
        if (user == null) {
            throw ConflictException(ErrorCode.USER_NOT_CREATED)
        }
    }
}
