package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.directroom.DirectChatRoomAppender
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomReader
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomRemover
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomUpdater
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomValidator
import org.chewing.v1.implementation.chat.sequence.ChatSequenceFinder
import org.chewing.v1.implementation.chat.sequence.ChatSequenceHandler
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.chat.room.ChatRoomMemberType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.DirectChatSequence
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class DirectChatRoomService(
    private val directChatRoomReader: DirectChatRoomReader,
    private val directChatRoomRemover: DirectChatRoomRemover,
    private val directChatRoomUpdater: DirectChatRoomUpdater,
    private val directChatRoomValidator: DirectChatRoomValidator,
    private val directChatRoomAppender: DirectChatRoomAppender,
    private val chatSequenceFinder: ChatSequenceFinder,
    private val chatSequenceHandler: ChatSequenceHandler
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
            directChatRoomUpdater.updateMemberStatus(userId, existingChatRoom.chatRoomId, ChatRoomMemberStatus.ACTIVE)
            val chatRoomMemberInfo = directChatRoomReader.readMemberInfo(userId, existingChatRoom.chatRoomId)
            val chatLogSequence = chatSequenceFinder.findCurrentRoomSequence(existingChatRoom.chatRoomId)
            val chatMemberSequence = chatSequenceHandler.handleMemberJoinSequence(existingChatRoom.chatRoomId, userId, chatLogSequence)
            DirectChatRoom.of(existingChatRoom,chatRoomMemberInfo, chatLogSequence, chatMemberSequence)
        } else {
            val newChatRoomInfo = directChatRoomAppender.appendRoom(userId, friendId)

            directChatRoomAppender.appendMember(userId, newChatRoomInfo.chatRoomId)
            directChatRoomAppender.appendMember(friendId, newChatRoomInfo.chatRoomId)

            val chatRoomMemberInfo = directChatRoomReader.readMemberInfo(userId, newChatRoomInfo.chatRoomId)
            val chatLogSequence = chatSequenceFinder.findCurrentRoomSequence(newChatRoomInfo.chatRoomId)
            val chatMemberSequence = chatSequenceHandler.handleMemberJoinSequence(newChatRoomInfo.chatRoomId,userId, chatLogSequence)
            DirectChatRoom.of(newChatRoomInfo,chatRoomMemberInfo, chatLogSequence, chatMemberSequence)
        }
    }

    fun readDirectChatRoom(userId: UserId, chatRoomId: ChatRoomId): DirectChatSequence {
        val chatLogSequence = chatSequenceFinder.findCurrentRoomSequence(chatRoomId)
        return chatSequenceHandler.handleMemberReadSequence(chatRoomId, userId, chatLogSequence)
    }


    fun getDirectChatRooms(userId: UserId): List<DirectChatRoom> {
        val chatRooms = directChatRoomReader.readRoomInfos(userId)
        val ownedChatRooms = directChatRoomReader.readsMemberInfos(chatRooms.map{it.chatRoomId}, userId)
        val chatRoomSequences = chatSequenceFinder.findCurrentRoomSequences(chatRooms.map{it.chatRoomId})
        val memberSequences = chatSequenceFinder.findCurrentMemberSequences(chatRooms.map{it.chatRoomId}, userId)
        return chatRooms.map { chatRoom ->
            val ownedChatRoom = ownedChatRooms.find { it.id == chatRoom.chatRoomId }
            val chatRoomSequence = chatRoomSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val memberSequence = memberSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            DirectChatRoom.of(chatRoom, ownedChatRoom!!, chatRoomSequence!!, memberSequence!!)
        }
    }

    fun increaseDirectChatRoomSequence(chatRoomId: ChatRoomId): DirectChatSequence {
        return chatSequenceHandler.handleRoomIncreaseSequence(chatRoomId)
    }
}
