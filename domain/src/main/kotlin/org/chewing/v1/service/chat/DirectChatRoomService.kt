package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.directroom.DirectChatRoomHandler
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomReader
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomRemover
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomUpdater
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomValidator
import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.chat.room.ChatRoomMemberType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatLogSequence
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class DirectChatRoomService(
    private val directChatRoomReader: DirectChatRoomReader,
    private val directChatRoomRemover: DirectChatRoomRemover,
    private val directChatRoomUpdater: DirectChatRoomUpdater,
    private val directChatRoomValidator: DirectChatRoomValidator,
    private val directChatRoomHandler: DirectChatRoomHandler
) {
    fun deleteDirectChatRoom(chatLogSequence: ChatLogSequence, userId: UserId, chatRoomId: ChatRoomId) {
        val existingChatRoom = directChatRoomReader.readRoomInfo(chatRoomId, userId)
        directChatRoomRemover.removeMember(userId, chatRoomId)
        directChatRoomUpdater.updateStartSequence(userId,existingChatRoom.chatRoomId, chatLogSequence)
    }

    fun updateDirectChatRoomType(userId: UserId, chatRoomId: ChatRoomId, type: ChatRoomMemberType) {
        directChatRoomUpdater.updateType(userId, chatRoomId, type)
    }

    fun produceDirectChatRoom(userId: UserId, friendId: UserId): DirectChatLogSequence {
        directChatRoomValidator.isNotSelf(userId, friendId)
        val existingChatRoom = directChatRoomReader.readRoomInfoByRelation(userId, friendId)
        return if (existingChatRoom != null) {
            val chatRoomId = directChatRoomHandler.handleExistingChatRoom(existingChatRoom, userId)
            directChatRoomReader.readRoomSequence(chatRoomId, userId)
        } else {
            val chatRoomId = directChatRoomHandler.handleCreateChatRoom(userId, friendId)
            directChatRoomReader.readRoomSequence(chatRoomId, userId)
        }
    }

    fun updateDirectChatRoomReadSequence(userId: UserId, chatRoomId: ChatRoomId, chatLogSequence: ChatLogSequence) {
        directChatRoomUpdater.updateReadSequence(userId, chatRoomId, chatLogSequence)
    }

    fun getDirectChatRooms(userId: UserId){
        val chatRooms = directChatRoomReader.readRoomInfos(userId)
        val ownedChatRooms = directChatRoomReader.readsMemberInfos(chatRooms.map{it.chatRoomId}, userId)
        val chatRoomSequence = directChatRoomReader.readsRoomSequence(chatRooms.map{it.chatRoomId}, userId)
    }
}
