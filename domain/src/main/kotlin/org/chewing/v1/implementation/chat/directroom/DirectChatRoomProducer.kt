package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomProducer(
    private val directChatRoomRepository: DirectChatRoomRepository,
    private val directChatRoomUpdater: DirectChatRoomUpdater,
    private val directChatRoomAppender: DirectChatRoomAppender
) {
    fun produceRoom(userId: UserId, friendId: UserId): DirectChatRoomInfo {
        val chatRoomInfo = directChatRoomRepository.readInfo(userId, friendId)
        return if (chatRoomInfo != null) {
            directChatRoomUpdater.updateStatus(userId, chatRoomInfo.directChatRoomId, ChatRoomMemberStatus.ACTIVE)
            chatRoomInfo
        } else {
            val chatRoomInfo = directChatRoomAppender.appendRoom()
            directChatRoomAppender.appendMember(userId, chatRoomInfo.directChatRoomId)
            directChatRoomAppender.appendMember(friendId, chatRoomInfo.directChatRoomId)
            chatRoomInfo
        }
    }
}
