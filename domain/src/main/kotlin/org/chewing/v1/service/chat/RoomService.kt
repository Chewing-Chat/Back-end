package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.room.*
import org.chewing.v1.implementation.chat.sequence.ChatFinder
import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.chat.room.ChatRoomInfo
import org.chewing.v1.model.chat.room.Room
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class RoomService(
    private val chatRoomReader: ChatRoomReader,
    private val chatRoomRemover: ChatRoomRemover,
    private val chatRoomEnricher: ChatRoomEnricher,
    private val chatRoomAppender: ChatRoomAppender,
    private val chatRoomHandler: ChatRoomHandler,
    private val chatRoomValidator: ChatRoomValidator,
    private val chatFinder: ChatFinder,
) {
    fun getChatRooms(userId: UserId): List<Room> {
        val chatRoomMembers = chatRoomReader.readOwnedChatRoomMembers(userId)
        val chatRoomInfos = chatRoomReader.reads(chatRoomMembers.map { it.chatRoomId }.distinct())
        return chatRoomEnricher.enrichChatRooms(
            chatRoomMembers,
            chatRoomInfos,
            userId,
        )
    }

    fun getChatRoom(chatRoomId: String): ChatRoomInfo {
        return chatRoomReader.readChatRoom(chatRoomId)
    }

    fun deleteGroupChatRooms(chatRoomIds: List<String>, userId: UserId) {
        chatRoomRemover.removeGroups(chatRoomIds, userId)
    }

    fun deleteChatRoom(chatRoomIds: List<String>, userId: UserId) {
        chatRoomRemover.removePersonals(chatRoomIds, userId)
    }

    fun createChatRoom(userId: UserId, friendId: UserId): String {
        val chatRoomId = chatRoomReader.readPersonalChatRoomId(userId, friendId)
        if (chatRoomId != null) {
            val number = chatFinder.findCurrentNumber(chatRoomId)
            chatRoomAppender.appendIfNotExistPersonalMember(chatRoomId, userId, friendId, number)
            return chatRoomId
        } else {
            val newRoomId = chatRoomAppender.append(false)
            val newChatNumber = ChatNumber.of(newRoomId, 0, 0)
            chatRoomAppender.appendIfNotExistPersonalMember(newRoomId, userId, friendId, newChatNumber)
            return newRoomId
        }
    }

    fun createGroupChatRoom(userId: UserId, friendIds: List<UserId>): String {
        val newRoomId = chatRoomAppender.append(true)
        val number = chatFinder.findCurrentNumber(newRoomId)
        chatRoomAppender.appendGroupMembers(newRoomId, friendIds, number)
        return newRoomId
    }

    fun getChatRoomFriends(chatRoomId: String, userId: UserId, chatRoomInfo: ChatRoomInfo): List<ChatRoomMemberInfo> {
        chatRoomInfo.isGroup.let {
            if (it) {
                return chatRoomReader.readGroupFriend(chatRoomId, userId)
            } else {
                return listOf(chatRoomReader.readPersonalFriend(chatRoomId, userId))
            }
        }
    }

    fun activateChatRoom(chatRoomId: String, userId: UserId, number: ChatNumber): ChatRoomInfo {
        val chatRoom = chatRoomReader.readChatRoom(chatRoomId)
        if (!chatRoom.isGroup) {
            val friendMember = chatRoomReader.readPersonalFriend(chatRoomId, userId)
            chatRoomAppender.appendIfNotExistPersonalMember(chatRoomId, userId, friendMember.memberId, number)
        }
        return chatRoom
    }

    fun inviteChatRoom(chatRoomId: String, friendId: UserId, userId: UserId) {
        chatRoomValidator.validateGroupChatRoom(chatRoomId)
        val number = chatFinder.findCurrentNumber(chatRoomId)
        chatRoomAppender.appendInviteMember(chatRoomId, friendId, number)
    }

    fun favoriteChatRoom(chatRoomId: String, userId: UserId, favorite: Boolean) {
        val chatRoomInfo = chatRoomReader.readChatRoom(chatRoomId)
        chatRoomHandler.lockFavoriteChatRoom(chatRoomInfo.chatRoomId, userId, favorite, chatRoomInfo.isGroup)
    }

    fun updateReadChatRoom(chatRoomId: String, userId: UserId, number: ChatNumber) {
        val chatRoomInfo = chatRoomReader.readChatRoom(chatRoomId)
        chatRoomHandler.lockReadChatRoom(userId, number, chatRoomInfo.isGroup)
    }
}
