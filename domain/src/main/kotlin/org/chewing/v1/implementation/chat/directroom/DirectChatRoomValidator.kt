package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class DirectChatRoomValidator {
    fun isNotSelf(userId: UserId, friendId: UserId) {
        if (userId == friendId) {
            throw ConflictException(ErrorCode.CHATROOM_NOT_SELF)
        }
    }
    fun isNotActivated(chatRoomInfo: DirectChatRoomInfo?) {
        if (chatRoomInfo != null && chatRoomInfo.status != ChatRoomMemberStatus.DELETED) {
            throw ConflictException(ErrorCode.CHATROOM_CREATE_FAILED)
        }
    }
    fun isActivated(chatRoomInfo: DirectChatRoomInfo?) {
        if (chatRoomInfo == null || chatRoomInfo.status == ChatRoomMemberStatus.DELETED) {
            throw NotFoundException(ErrorCode.CHATROOM_NOT_FOUND)
        }
    }
}
