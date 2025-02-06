package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.directroom.DirectChatRoomAppender
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomReader
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomRemover
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomUpdater
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomValidator
import org.chewing.v1.implementation.chat.sequence.ChatSequenceFinder
import org.chewing.v1.implementation.chat.sequence.ChatSequenceHandler
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatSequence
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
    private val chatSequenceHandler: ChatSequenceHandler,
) {
    //채팅방 삭제 -> soft delete
    fun deleteDirectChatRoom(userId: UserId, chatRoomId: ChatRoomId) {
        directChatRoomRemover.removeMember(userId, chatRoomId)
    }

    //채팅방 타입 변경 -> favorite, normal
    fun favoriteDirectChatRoomType(userId: UserId, chatRoomId: ChatRoomId) {
        directChatRoomUpdater.updateMemberStatus(userId, chatRoomId, ChatRoomMemberStatus.FAVORITE)
    }

    // 채팅방 제공
    fun produceDirectChatRoom(userId: UserId, friendId: UserId): DirectChatRoom {
        directChatRoomValidator.isNotSelf(userId, friendId)
        val existingChatRoom = directChatRoomReader.readRoomInfoByRelation(userId, friendId)
        return if (existingChatRoom != null) {
            if (existingChatRoom.status == ChatRoomMemberStatus.DELETED) {
                directChatRoomUpdater.updateMemberStatus(userId, existingChatRoom.chatRoomId, ChatRoomMemberStatus.ACTIVE)
                val chatRoomSequence = chatSequenceFinder.findCurrentRoomSequence(existingChatRoom.chatRoomId)
                val chatMemberSequence = chatSequenceHandler.handleJoinMemberSequence(existingChatRoom.chatRoomId, userId, chatRoomSequence)
                DirectChatRoom.of(existingChatRoom, chatRoomSequence, chatMemberSequence)
            } else {
                val chatRoomSequence = chatSequenceFinder.findCurrentRoomSequence(existingChatRoom.chatRoomId)
                val chatMemberSequence = chatSequenceFinder.findCurrentMemberSequence(existingChatRoom.chatRoomId, userId)
                DirectChatRoom.of(existingChatRoom, chatRoomSequence, chatMemberSequence)
            }
        } else {
            val newChatRoomInfo = directChatRoomAppender.appendRoom(userId, friendId)
            val chatRoomSequence = chatSequenceHandler.handleCreateRoomSequence(newChatRoomInfo.chatRoomId)
            val memberIds = listOf(userId, friendId)
            chatSequenceHandler.handleCreateMemberSequences(newChatRoomInfo.chatRoomId, memberIds)
            val chatMemberSequence = chatSequenceFinder.findCurrentMemberSequence(newChatRoomInfo.chatRoomId, userId)
            DirectChatRoom.of(newChatRoomInfo, chatRoomSequence, chatMemberSequence)
        }
    }

    fun readDirectChatRoom(userId: UserId, chatRoomId: ChatRoomId): ChatSequence {
        val chatLogSequence = chatSequenceFinder.findCurrentRoomSequence(chatRoomId)
        return chatSequenceHandler.handleMemberReadSequence(chatRoomId, userId, chatLogSequence)
    }

    fun getDirectChatRooms(userId: UserId): List<DirectChatRoom> {
        val chatRooms = directChatRoomReader.readRoomInfos(userId)
        val chatRoomSequences = chatSequenceFinder.findCurrentRoomSequences(chatRooms.map { it.chatRoomId })
        val memberSequences = chatSequenceFinder.findCurrentMemberSequences(chatRooms.map { it.chatRoomId }, userId)
        return chatRooms.map { chatRoom ->
            val chatRoomSequence = chatRoomSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val memberSequence = memberSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            DirectChatRoom.of(chatRoom, chatRoomSequence!!, memberSequence!!)
        }
    }

    fun increaseDirectChatRoomSequence(chatRoomId: ChatRoomId): ChatSequence {
        return chatSequenceHandler.handleRoomIncreaseSequence(chatRoomId)
    }
}
