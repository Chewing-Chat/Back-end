package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomAppender
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomReader
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomRemover
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomUpdater
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomValidator
import org.chewing.v1.implementation.chat.sequence.ChatSequenceFinder
import org.chewing.v1.implementation.chat.sequence.ChatSequenceHandler
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatSequence
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
    fun produceGroupChatRoom(userId: UserId, friendIds: List<UserId>, groupName: String): GroupChatRoom {
        val memberIds = friendIds + userId
        val newChatRoomInfo = groupChatRoomAppender.appendRoom(groupName)
        val chatRoomMemberInfos = groupChatRoomAppender.appendMembers(newChatRoomInfo.chatRoomId, memberIds)
        val chatRoomSequence = chatSequenceHandler.handleCreateRoomSequence(newChatRoomInfo.chatRoomId)
        chatSequenceHandler.handleCreateMemberSequences(newChatRoomInfo.chatRoomId, memberIds)
        val chatMemberSequence = chatSequenceFinder.findCurrentMemberSequence(newChatRoomInfo.chatRoomId, userId)
        return GroupChatRoom.of(newChatRoomInfo, chatRoomMemberInfos, chatRoomSequence, chatMemberSequence)
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

    fun favoriteGroupChatRoomType(userId: UserId, chatRoomId: ChatRoomId) {
        groupChatRoomValidator.isParticipant(chatRoomId, userId)
        groupChatRoomUpdater.updateMemberStatus(chatRoomId, userId, ChatRoomMemberStatus.FAVORITE)
    }

    fun getGroupChatRooms(userId: UserId): List<GroupChatRoom> {
        val userParticipatedRooms = groupChatRoomReader.readRoomUserInfos(userId)
        val chatRooms = groupChatRoomReader.readRoomInfos(userParticipatedRooms.map { it.chatRoomId })
        val chatRoomMembers = groupChatRoomReader.readRoomMemberInfos(chatRooms.map { it.chatRoomId })
        val chatRoomSequences = chatSequenceFinder.findCurrentRoomSequences(chatRooms.map { it.chatRoomId })
        val memberSequences = chatSequenceFinder.findCurrentMemberSequences(chatRooms.map { it.chatRoomId }, userId)
        return chatRooms.map { chatRoom ->
            val chatRoomSequence = chatRoomSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val memberSequence = memberSequences.find { it.chatRoomId == chatRoom.chatRoomId }
            val chatRoomMemberInfos = chatRoomMembers.filter { it.chatRoomId == chatRoom.chatRoomId }
            GroupChatRoom.of(chatRoom, chatRoomMemberInfos, chatRoomSequence!!, memberSequence!!)
        }
    }

    fun readGroupChatRoom(userId: UserId, chatRoomId: ChatRoomId): ChatSequence {
        val chatLogSequence = chatSequenceFinder.findCurrentRoomSequence(chatRoomId)
        return chatSequenceHandler.handleMemberReadSequence(chatRoomId, userId, chatLogSequence)
    }
}
