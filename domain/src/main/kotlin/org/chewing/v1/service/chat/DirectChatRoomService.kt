package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.directroom.DirectChatRoomProducer
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomReader
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomRemover
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomUpdater
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomValidator
import org.chewing.v1.model.chat.room.ChatRoomMemberType
import org.chewing.v1.model.chat.room.DirectChatRoomId
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class DirectChatRoomService(
    private val directChatRoomReader: DirectChatRoomReader,
    private val directChatRoomRemover: DirectChatRoomRemover,
    private val directChatRoomProducer: DirectChatRoomProducer,
    private val directChatRoomUpdater: DirectChatRoomUpdater,
    private val directChatRoomValidator: DirectChatRoomValidator
) {
    fun produceDirectChatRoom(userId: UserId, friendId: UserId): DirectChatRoomInfo {
        directChatRoomValidator.isNotSelf(userId,friendId)
        return directChatRoomProducer.produceRoom(userId, friendId)
    }

    fun deleteDirectChatRoom(userId: UserId, chatRoomId: DirectChatRoomId) {
        directChatRoomRemover.removeMember(userId, chatRoomId)
    }

    fun getDirectChatRoomInfo(userId: UserId, friendId: UserId): DirectChatRoomInfo {
        return directChatRoomReader.readRoomInfo(userId, friendId)
    }

    fun updateDirectChatRoomType(userId: UserId, chatRoomId: DirectChatRoomId, type: ChatRoomMemberType) {
        directChatRoomUpdater.updateType(userId, chatRoomId, type)
    }
}
