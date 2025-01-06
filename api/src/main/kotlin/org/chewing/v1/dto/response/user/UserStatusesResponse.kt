package org.chewing.v1.dto.response.user

import org.chewing.v1.model.user.UserStatus

data class UserStatusesResponse(
    val statuses: List<UserStatusResponse>,
) {
    companion object {
        fun of(
            statuses: List<UserStatus>,
        ): UserStatusesResponse {
            return UserStatusesResponse(
                statuses = statuses.map {
                    UserStatusResponse.of(it)
                },
            )
        }
    }

    data class UserStatusResponse(
        val statusId: String,
        val message: String,
        val emoji: String,
        val selected: Boolean,
    ) {
        companion object {
            fun of(
                userStatus: UserStatus,
            ): UserStatusResponse {
                return UserStatusResponse(
                    statusId = userStatus.statusId,
                    message = userStatus.message,
                    emoji = userStatus.emoji,
                    selected = userStatus.isSelected,
                )
            }
        }
    }
}
