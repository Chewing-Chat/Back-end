package org.chewing.v1.implementation.user

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.auth.PushInfo
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

    fun reads(userIds: List<UserId>, status: AccessStatus): List<UserInfo> {
        return userRepository.reads(userIds, status)
    }

    fun readPushTokens(userId: UserId): List<PushInfo> {
        return pushNotificationRepository.readAll(userId)
    }

    fun readsPushTokens(userIds: List<UserId>): List<PushInfo> {
        return pushNotificationRepository.readsAll(userIds)
    }

    fun readPushToken(userId: UserId, deviceId: String): PushInfo {
        return pushNotificationRepository.read(userId, deviceId)
            ?: throw NotFoundException(ErrorCode.USER_PUSH_TOKEN_NOT_FOUND)
    }
}
