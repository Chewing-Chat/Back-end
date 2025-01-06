package org.chewing.v1.implementation.user.status

import org.chewing.v1.repository.user.UserStatusRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserStatusUpdater(
    private val statusRepository: UserStatusRepository,
) {
    @Transactional
    fun updateSelectedTrue(userId: String, statusId: String) {
        statusRepository.updateSelectedStatusFalse(userId)
        statusRepository.updateSelectedStatusTrue(userId, statusId)
    }

    fun updateDeselected(userId: String) {
        statusRepository.updateSelectedStatusFalse(userId)
    }
}
