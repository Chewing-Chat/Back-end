package org.chewing.v1.service.user

import org.chewing.v1.implementation.user.status.UserStatusAppender
import org.chewing.v1.implementation.user.status.UserStatusReader
import org.chewing.v1.implementation.user.status.UserStatusRemover
import org.chewing.v1.implementation.user.status.UserStatusUpdater
import org.chewing.v1.model.user.UserStatus
import org.springframework.stereotype.Service

@Service
class UserStatusService(
    private val userStatusReader: UserStatusReader,
    private val userStatusUpdater: UserStatusUpdater,
    private val userStatusRemover: UserStatusRemover,
    private val userStatusAppender: UserStatusAppender,

) {
    fun getUserStatuses(userId: String): List<UserStatus> = userStatusReader.reads(userId)

    fun getSelectedStatuses(userIds: List<String>): List<UserStatus> = userStatusReader.readsSelected(userIds)

    fun getSelectedStatus(userId: String): UserStatus = userStatusReader.readSelected(userId)

    fun selectUserStatus(userId: String, statusId: String) {
        userStatusUpdater.updateSelectedTrue(userId, statusId)
    }

    fun deleteSelectUserStatus(userId: String) {
        userStatusUpdater.updateDeselected(userId)
    }

    fun deleteUserStatuses(statusesId: List<String>) {
        userStatusRemover.removes(statusesId)
    }

    fun createUserStatus(userId: String, message: String, emoji: String) {
        userStatusAppender.append(userId, message, emoji)
    }
    fun deleteAllUserStatuses(userId: String) {
        userStatusRemover.removeUsers(userId)
    }
}
