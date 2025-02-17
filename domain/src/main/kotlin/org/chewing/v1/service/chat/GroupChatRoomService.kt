package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomAppender
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomReader
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomRemover
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomUpdater
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomValidator
import org.chewing.v1.implementation.chat.sequence.ChatSequenceFinder
import org.chewing.v1.implementation.chat.sequence.ChatSequenceHandler
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.GroupChatRoom
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class GroupChatRoomService(
    private val groupChatRoomAppender: GroupChatRoomAppender,
    private val groupChatRoomReader: GroupChatRoomReader,
    private val groupChatRoomRemover: GroupChatRoomRemover,
    private val groupChatRoomUpdater: GroupChatRoomUpdater,
    private val groupChatRoomValidator: GroupChatRoomValidator,
    private val chatSequenceFinder: ChatSequenceFinder,
    private val chatSequenceHandler: ChatSequenceHandler,
) {
    fun produceGroupChatRoom(userId: UserId, friendIds: List<UserId>, groupName: String): ChatRoomId {
        val memberIds = friendIds + userId
        val newChatRoomInfo = groupChatRoomAppender.appendRoom(groupName)
        groupChatRoomAppender.appendMembers(newChatRoomInfo.chatRoomId, memberIds)
        chatSequenceHandler.handleCreateRoomSequence(newChatRoomInfo.chatRoomId)
        chatSequenceHandler.handleCreateMemberSequences(newChatRoomInfo.chatRoomId, memberIds)
        return newChatRoomInfo.chatRoomId
    }

    fun deleteGroupChatRoom(userId: UserId, chatRoomId: ChatRoomId) {
        groupChatRoomValidator.isParticipant(chatRoomId, userId)
        groupChatRoomRemover.removeMember(chatRoomId, userId)
    }

    fun inviteGroupChatRoom(userId: UserId, chatRoomId: ChatRoomId, friendId: UserId) {
        groupChatRoomValidator.isParticipant(chatRoomId, userId)
        groupChatRoomAppender.appendMember(chatRoomId, friendId)
        val chatRoomSequence = chatSequenceFinder.findCurrentRoomSequence(chatRoomId)
        chatSequenceHandler.handleCreateRoomSequence(chatRoomId)
        chatSequenceHandler.handleJoinMemberSequence(chatRoomId, friendId, chatRoomSequence)
    }

    fun favoriteGroupChatRoomType(userId: UserId, chatRoomId: ChatRoomId, status: ChatRoomMemberStatus) {
        groupChatRoomValidator.isParticipant(chatRoomId, userId)
        groupChatRoomUpdater.updateMemberStatus(chatRoomId, userId, status)
    }

    fun validateIsParticipant(chatRoomId: ChatRoomId, userId: UserId) {
        groupChatRoomValidator.isParticipant(chatRoomId, userId)
    }

    fun getGroupChatRooms(userId: UserId): List<GroupChatRoom> {
        val userParticipatedRooms = groupChatRoomReader.readRoomUserInfos(userId)
        val chatRoomIds = userParticipatedRooms.map { it.chatRoomId }
        val chatRooms = groupChatRoomReader.readRoomInfos(chatRoomIds)
        val chatRoomMembers = groupChatRoomReader.readsRoomMemberInfos(chatRoomIds)
        val chatRoomSequences = chatSequenceFinder.findCurrentRoomSequences(chatRoomIds)
        val memberSequences = chatSequenceFinder.findCurrentMemberSequences(chatRoomIds, userId)

        return chatRooms.mapNotNull { chatRoom ->
            val chatRoomSequence = chatRoomSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val memberSequence = memberSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val members = chatRoomMembers.filter { it.chatRoomId == chatRoom.chatRoomId }
            if (chatRoomSequence != null && memberSequence != null) {
                GroupChatRoom.of(chatRoom, members, chatRoomSequence, memberSequence)
            } else {
                null
            }
        }
    }

    fun searchGroupChatRooms(userId: UserId, friendIds: List<UserId>): List<GroupChatRoom> {
        val userParticipatedRooms = groupChatRoomReader.readRoomUserInfos(userId)
        val chatRoomIds = userParticipatedRooms.map { it.chatRoomId }
        val chatRooms = groupChatRoomReader.readRoomInfos(chatRoomIds)
        val chatRoomMembers = groupChatRoomReader.readsRoomMemberInfos(chatRoomIds)
        val chatRoomSequences = chatSequenceFinder.findCurrentRoomSequences(chatRoomIds)
        val memberSequences = chatSequenceFinder.findCurrentMemberSequences(chatRoomIds, userId)

        return chatRooms.mapNotNull { chatRoom ->
            val chatRoomSequence = chatRoomSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val memberSequence = memberSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val members = chatRoomMembers.filter { it.chatRoomId == chatRoom.chatRoomId }

            // 친구 ID가 하나라도 포함되어 있으면 검색되도록 수정
            if (chatRoomSequence != null && memberSequence != null && members.any { friendIds.contains(it.memberId) }) {
                GroupChatRoom.of(chatRoom, members, chatRoomSequence, memberSequence)
            } else {
                null
            }
        }
    }

    fun getUnreadGroupChatRooms(userId: UserId): List<GroupChatRoom> {
        val userParticipatedRooms = groupChatRoomReader.readRoomUserInfos(userId)
        val chatRoomIds = userParticipatedRooms.map { it.chatRoomId }
        val chatRooms = groupChatRoomReader.readRoomInfos(chatRoomIds)
        val chatRoomMembers = groupChatRoomReader.readsRoomMemberInfos(chatRoomIds)
        val chatRoomSequences = chatSequenceFinder.findCurrentRoomSequences(chatRoomIds)
        val memberSequences = chatSequenceFinder.findCurrentMemberSequences(chatRoomIds, userId)

        return chatRooms.mapNotNull { chatRoom ->
            val chatRoomSequence = chatRoomSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val memberSequence = memberSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val members = chatRoomMembers.filter { it.chatRoomId == chatRoom.chatRoomId }
            if (chatRoomSequence != null && memberSequence != null && memberSequence.readSequenceNumber < chatRoomSequence.sequenceNumber) {
                GroupChatRoom.of(chatRoom, members, chatRoomSequence, memberSequence)
            } else {
                null
            }
        }
    }

    fun readGroupChatRoom(userId: UserId, chatRoomId: ChatRoomId, sequenceNumber: Int): ChatRoomMemberSequence {
        return chatSequenceHandler.handleMemberReadSequence(chatRoomId, userId, sequenceNumber)
    }

    fun increaseGroupChatRoomSequence(chatRoomId: ChatRoomId): ChatRoomSequence {
        return chatSequenceHandler.handleRoomIncreaseSequence(chatRoomId)
    }

    fun getGroupChatRoom(userId: UserId, chatRoomId: ChatRoomId): GroupChatRoom {
        val existingChatRoom = groupChatRoomReader.readRoomInfo(chatRoomId)
        val chatRoomSequence = chatSequenceFinder.findCurrentRoomSequence(chatRoomId)
        val chatMemberSequence = chatSequenceFinder.findCurrentMemberSequence(chatRoomId, userId)
        val chatRoomMemberInfos = groupChatRoomReader.readRoomMemberInfos(chatRoomId)
        return GroupChatRoom.of(existingChatRoom, chatRoomMemberInfos, chatRoomSequence, chatMemberSequence)
    }
}
