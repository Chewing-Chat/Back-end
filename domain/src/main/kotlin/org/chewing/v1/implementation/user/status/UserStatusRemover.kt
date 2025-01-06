package org.chewing.v1.implementation.user.status

import org.chewing.v1.repository.user.UserStatusRepository
import org.springframework.stereotype.Component

@Component
class UserStatusRemover(
    private val statusRepository: UserStatusRepository,
) {

    fun removeUsers(userId: String) {
        statusRepository.removeAllByUserId(userId)
    }

    fun removes(statusesId: List<String>) {
        statusRepository.removes(statusesId)
    }
}
