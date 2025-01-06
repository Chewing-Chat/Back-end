package org.chewing.v1.implementation.user.status

import org.chewing.v1.model.user.UserStatus
import org.chewing.v1.repository.user.UserStatusRepository
import org.springframework.stereotype.Component

@Component
class UserStatusReader(
    private val userStatusRepository: UserStatusRepository,
) {

    fun readsSelected(userIds: List<String>): List<UserStatus> {
        return userStatusRepository.readSelectedUsers(userIds)
    }

    fun readSelected(userId: String): UserStatus {
        return userStatusRepository.readSelected(userId)
    }

    fun reads(userId: String): List<UserStatus> {
        return userStatusRepository.reads(userId)
    }
}
