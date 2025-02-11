package org.chewing.v1.implementation.user.user

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.PushNotificationRepository
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Component


@Component
class UserReader(
    private val userRepository: UserRepository,
    private val pushNotificationRepository: PushNotificationRepository,
) {
    fun read(userId: UserId, status: AccessStatus): UserInfo {
        return userRepository.read(userId, status) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
    }

    fun readByContact(contact: Contact, accessStatus: AccessStatus): UserInfo {
        return userRepository.readByContact(contact, accessStatus) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
    }

    fun readsByContacts(contacts: List<Contact>, accessStatus: AccessStatus): List<UserInfo> {
        return userRepository.readsByContacts(contacts, accessStatus)
    }

    fun reads(userIds: List<UserId>): List<UserInfo> {
        return userRepository.reads(userIds)
    }

    fun readsPushToken(userId: UserId): List<PushToken> {
        return pushNotificationRepository.reads(userId)
    }
}
