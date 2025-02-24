package org.chewing.v1.implementation.user

import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.user.UserId
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.repository.user.PushNotificationRepository
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Component

@Component
class UserAppender(
    private val userRepository: UserRepository,
    private val pushNotificationRepository: PushNotificationRepository,
) {

    fun appendUserPushToken(userInfo: UserInfo, appToken: String, device: PushToken.Device) {
        pushNotificationRepository.append(device, appToken, userInfo)
    }

    fun append(contact: Contact, userName: String): UserInfo {
        return userRepository.append(contact, userName)
    }

    fun appendPassword(userId: UserId, password: String) {
        userRepository.appendPassword(userId, password)
    }
}
