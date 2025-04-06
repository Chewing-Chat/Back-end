package org.chewing.v1.implementation.user

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.auth.PushInfo
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.PushNotificationRepository
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Component

@Component
class UserRemover(
    private val userRepository: UserRepository,
    private val pushNotificationRepository: PushNotificationRepository,
) {
    fun remove(userId: UserId): UserInfo = userRepository.remove(userId) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)

    fun removePushToken(device: PushInfo.Device) {
        pushNotificationRepository.remove(device)
    }
}
