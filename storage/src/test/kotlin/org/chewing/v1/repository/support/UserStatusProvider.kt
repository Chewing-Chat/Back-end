package org.chewing.v1.repository.support

import org.chewing.v1.model.user.UserStatus
import org.springframework.stereotype.Component

@Component
object UserStatusProvider {

    fun buildSelected(userId: String): UserStatus = UserStatus.of("statusId", userId, "message", "emoji", true)

    fun buildNotSelected(userId: String): UserStatus = UserStatus.of("statusId", userId, "message", "emoji", false)
}
