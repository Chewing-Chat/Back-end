package org.chewing.v1.repository.user

import org.chewing.v1.model.user.UserStatus

interface UserStatusRepository {
    fun removes(statusesId: List<String>)
    fun readSelected(userId: String): UserStatus
    fun readSelectedUsers(userIds: List<String>): List<UserStatus>
    fun removeAllByUserId(userId: String)
    fun updateSelectedStatusTrue(userId: String, statusId: String)
    fun updateSelectedStatusFalse(userId: String)
    fun append(userId: String, statusMessage: String, emoji: String)
    fun reads(userId: String): List<UserStatus>
}
