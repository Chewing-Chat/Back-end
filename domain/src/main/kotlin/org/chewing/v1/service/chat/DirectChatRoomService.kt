package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.directroom.DirectChatRoomAppender
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomEnricher
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomReader
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomRemover
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomUpdater
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomValidator
import org.chewing.v1.implementation.chat.sequence.ChatSequenceFinder
import org.chewing.v1.implementation.chat.sequence.ChatSequenceHandler
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomSequence
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
    private val directChatRoomEnricher: DirectChatRoomEnricher,
) {
    //채팅방 0부터 시작
    //채팅방 삭제 -> soft delete
    fun deleteDirectChatRoom(userId: UserId, chatRoomId: ChatRoomId) {
        directChatRoomRemover.removeMember(userId, chatRoomId)
    }

    //채팅방 타입 변경 -> favorite, normal
    fun favoriteDirectChatRoomType(userId: UserId, chatRoomId: ChatRoomId, status: ChatRoomMemberStatus) {
        directChatRoomUpdater.updateMemberStatus(userId, chatRoomId, status)
    }

    fun restoreDirectChatRoom(userId: UserId, chatRoomId: ChatRoomId) {
        directChatRoomUpdater.updateMemberStatus(userId, chatRoomId, ChatRoomMemberStatus.NORMAL)
    }

    // 채팅방 제공
    //joinSequence -> 채팅방 입장 시 볼 수 있는 sequence 시작점
    fun getDirectChatRoom(userId: UserId, friendId: UserId): DirectChatRoom {
        directChatRoomValidator.isNotSelf(userId, friendId)
        val existingChatRoom = directChatRoomReader.readRoomInfoByRelation(userId, friendId)
        directChatRoomValidator.isActivated(existingChatRoom)
        val chatRoomSequences = chatSequenceFinder.findCurrentRoomSequence(existingChatRoom!!.chatRoomId)
        val memberSequences = chatSequenceFinder.findCurrentMemberSequence(existingChatRoom.chatRoomId, userId)
        return DirectChatRoom.of(existingChatRoom, chatRoomSequences, memberSequences)
    }

    fun createDirectChatRoom(userId: UserId, friendId: UserId): ChatRoomId {
        directChatRoomValidator.isNotSelf(userId, friendId)
        val existingChatRoom = directChatRoomReader.readRoomInfoByRelation(userId, friendId)
        directChatRoomValidator.isNotActivated(existingChatRoom)
        if (existingChatRoom == null) {
            val newChatRoom = directChatRoomAppender.appendRoom(userId, friendId)
            chatSequenceHandler.handleCreateRoomSequence(newChatRoom.chatRoomId)
            chatSequenceHandler.handleCreateMemberSequences(newChatRoom.chatRoomId, listOf(userId, friendId))
            return newChatRoom.chatRoomId
        } else {
            if (existingChatRoom.status == ChatRoomMemberStatus.DELETED) {
                directChatRoomUpdater.updateMemberStatus(userId, existingChatRoom.chatRoomId, ChatRoomMemberStatus.NORMAL)
            }
            return existingChatRoom.chatRoomId
        }
    }

    fun validateIsParticipant(userId: UserId, chatRoomId: ChatRoomId) {
        val existingChatRoom = directChatRoomReader.readRoomInfo(chatRoomId, userId)
        directChatRoomValidator.isActivated(existingChatRoom)
    }

    fun readDirectChatRoom(userId: UserId, chatRoomId: ChatRoomId, sequenceNumber: Int): ChatRoomMemberSequence {
        return chatSequenceHandler.handleMemberReadSequence(chatRoomId, userId, sequenceNumber)
    }

    fun getDirectChatRooms(userId: UserId): List<DirectChatRoom> {
        val chatRooms = directChatRoomReader.readRoomInfos(userId)
        val chatRoomSequences = chatSequenceFinder.findCurrentRoomSequences(chatRooms.map { it.chatRoomId })
        val memberSequences = chatSequenceFinder.findCurrentMemberSequences(chatRooms.map { it.chatRoomId }, userId)
        return directChatRoomEnricher.enrich(chatRooms, chatRoomSequences, memberSequences)
    }

    fun searchDirectChatRooms(userId: UserId, friendIds: List<UserId>): List<DirectChatRoom> {
        val chatRooms = directChatRoomReader.readRoomInfos(userId).filter { it.friendId in friendIds }
        val chatRoomSequences = chatSequenceFinder.findCurrentRoomSequences(chatRooms.map { it.chatRoomId })
        val memberSequences = chatSequenceFinder.findCurrentMemberSequences(chatRooms.map { it.chatRoomId }, userId)

        return directChatRoomEnricher.enrich(chatRooms, chatRoomSequences, memberSequences)
    }

    fun getUnReadDirectChatRooms(userId: UserId): List<DirectChatRoom> {
        val chatRooms = directChatRoomReader.readRoomInfos(userId)
        val chatRoomSequences = chatSequenceFinder.findCurrentRoomSequences(chatRooms.map { it.chatRoomId })
        val memberSequences = chatSequenceFinder.findCurrentMemberSequences(chatRooms.map { it.chatRoomId }, userId)
        return directChatRoomEnricher.enrichUnRead(chatRooms, chatRoomSequences, memberSequences)
    }

    fun getDirectChatRoom(userId: UserId, chatRoomId: ChatRoomId): DirectChatRoom {
        val existingChatRoom = directChatRoomReader.readRoomInfo(chatRoomId, userId)
        val chatRoomSequence = chatSequenceFinder.findCurrentRoomSequence(chatRoomId)
        val chatMemberSequence = chatSequenceFinder.findCurrentMemberSequence(chatRoomId, userId)
        return DirectChatRoom.of(existingChatRoom, chatRoomSequence, chatMemberSequence)
    }

    fun increaseDirectChatRoomSequence(chatRoomId: ChatRoomId): ChatRoomSequence {
        return chatSequenceHandler.handleRoomIncreaseSequence(chatRoomId)
    }
}
