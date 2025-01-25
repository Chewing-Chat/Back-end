package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class DirectChatRoomValidator {
    fun isNotSelf(userId: UserId, friendId: UserId) {
        if (userId == friendId) {
            throw ConflictException(ErrorCode.NOT_SUPPORT_FILE_TYPE)
        }
    }
}
