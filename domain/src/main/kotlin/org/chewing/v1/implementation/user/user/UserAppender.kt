package org.chewing.v1.implementation.user.user

import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.user.User
import org.chewing.v1.repository.user.PushNotificationRepository
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Component

@Component
class UserAppender(
    private val userRepository: UserRepository,
    private val pushNotificationRepository: PushNotificationRepository,
) {

    fun appendUserPushToken(user: User, appToken: String, device: PushToken.Device) {
        pushNotificationRepository.append(device, appToken, user)
    }

    fun appendIfNotExist(contact: Contact): User {
        return userRepository.append(contact)
    }
}
