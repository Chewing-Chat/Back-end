package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.chewing.v1.model.chat.room.DirectChatRoomMemberInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class DirectChatRoomHandler(
    private val directChatRoomReader: DirectChatRoomReader,
    private val directChatRoomUpdater: DirectChatRoomUpdater,
    private val directChatRoomAppender: DirectChatRoomAppender,
) {
    fun handleExistingChatRoom(existingChatRoom: DirectChatRoomInfo, userId: UserId): DirectChatRoomMemberInfo {
        val chatRoomId = existingChatRoom.chatRoomId
        val chatRoomUserInfo = directChatRoomReader.readMemberInfo(userId, chatRoomId)
        if (chatRoomUserInfo.status == ChatRoomMemberStatus.DELETED) {
            directChatRoomUpdater.updateMemberStatus(userId, chatRoomId, ChatRoomMemberStatus.ACTIVE)
        }
        return chatRoomUserInfo
    }
    fun handleCreateChatRoom(userId: UserId, friendId: UserId): DirectChatRoomInfo {
        val newChatRoomInfo = directChatRoomAppender.appendRoom(userId, friendId)

        listOf(userId, friendId).forEach { memberId ->
            directChatRoomAppender.appendMember(memberId, newChatRoomInfo.chatRoomId)
            directChatRoomAppender.appendSequence(newChatRoomInfo.chatRoomId, memberId)
        }

        return newChatRoomInfo
    }
}
