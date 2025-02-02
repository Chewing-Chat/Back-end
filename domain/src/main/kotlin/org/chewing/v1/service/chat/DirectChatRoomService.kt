package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.directroom.DirectChatRoomHandler
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomReader
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomRemover
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomUpdater
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomValidator
import org.chewing.v1.model.chat.room.DirectChatRoom
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
    //채팅방 삭제 -> soft delete
    fun deleteDirectChatRoom(userId: UserId, chatRoomId: ChatRoomId) {
        directChatRoomRemover.removeMember(userId, chatRoomId)
    }

    //채팅방 타입 변경 -> favorite, normal
    fun updateDirectChatRoomType(userId: UserId, chatRoomId: ChatRoomId, type: ChatRoomMemberType) {
        directChatRoomUpdater.updateRoomType(userId, chatRoomId, type)
    }

    // 채팅방 제공
    fun produceDirectChatRoom(userId: UserId, friendId: UserId): DirectChatRoom {
        directChatRoomValidator.isNotSelf(userId, friendId)
        val existingChatRoom = directChatRoomReader.readRoomInfoByRelation(userId, friendId)
        return if (existingChatRoom != null) {
            val chatRoomMemberInfo = directChatRoomHandler.handleExistingChatRoom(existingChatRoom, userId)
            val chatLogSequence = directChatRoomReader.readRoomSequence(chatRoomMemberInfo.id)
            val chatMemberSequence = directChatRoomUpdater.updateMemberJoinSequence(userId,existingChatRoom.chatRoomId, chatLogSequence)
            DirectChatRoom.of(existingChatRoom,chatRoomMemberInfo, chatLogSequence, chatMemberSequence)
        } else {
            val newChatRoom = directChatRoomHandler.handleCreateChatRoom(userId, friendId)
            val chatRoomMemberInfo = directChatRoomReader.readMemberInfo(userId, newChatRoom.chatRoomId)
            val chatLogSequence = directChatRoomReader.readRoomSequence(newChatRoom.chatRoomId)
            val chatMemberSequence = directChatRoomUpdater.updateMemberJoinSequence(userId,newChatRoom.chatRoomId, chatLogSequence)
            DirectChatRoom.of(newChatRoom,chatRoomMemberInfo, chatLogSequence, chatMemberSequence)
        }
    }

    fun readDirectChatRoom(userId: UserId, chatRoomId: ChatRoomId) {
        val chatLogSequence = directChatRoomReader.readRoomSequence(chatRoomId)
        directChatRoomUpdater.updateMemberReadSequence(userId, chatRoomId, chatLogSequence)
    }


    fun getDirectChatRooms(userId: UserId): List<DirectChatRoom> {
        val chatRooms = directChatRoomReader.readRoomInfos(userId)
        val ownedChatRooms = directChatRoomReader.readsMemberInfos(chatRooms.map{it.chatRoomId}, userId)
        val chatRoomSequences = directChatRoomReader.readsRoomSequence(chatRooms.map{it.chatRoomId})
        val memberSequences = directChatRoomReader.readsMemberSequences(chatRooms.map{it.chatRoomId}, userId)
        return chatRooms.map { chatRoom ->
            val ownedChatRoom = ownedChatRooms.find { it.id == chatRoom.chatRoomId }
            val chatRoomSequence = chatRoomSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val memberSequence = memberSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            DirectChatRoom.of(chatRoom, ownedChatRoom!!, chatRoomSequence!!, memberSequence!!)
        }
    }

    fun increaseDirectChatRoomSequence(chatRoomId: ChatRoomId): DirectChatLogSequence {
        return directChatRoomUpdater.updateIncreaseRoomSequence(chatRoomId)
    }
}
