package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.DirectChatRoomId
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface DirectChatRoomRepository {
    fun readInfo(userId: UserId, friendId: UserId) : DirectChatRoomInfo?
    fun append() : DirectChatRoomInfo
}
