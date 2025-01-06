package org.chewing.v1.implementation.chat.room

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.room.ChatRoomInfo
import org.chewing.v1.repository.chat.ChatRoomRepository
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.chewing.v1.repository.chat.PersonalChatRoomMemberRepository
import org.springframework.stereotype.Component

@Component
class ChatRoomReader(
    private val chatRoomRepository: ChatRoomRepository,
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository,
    private val personalChatRoomMemberRepository: PersonalChatRoomMemberRepository,
) {
    fun reads(chatRoomIds: List<String>): List<ChatRoomInfo> {
        return chatRoomRepository.readChatRooms(chatRoomIds)
    }

    fun readChatRoom(chatRoomId: String): ChatRoomInfo {
        return chatRoomRepository.readChatRoom(chatRoomId) ?: throw NotFoundException(ErrorCode.CHATROOM_NOT_FOUND)
    }

    fun readPersonalChatRoomId(userId: String, friendId: String): String? {
        return personalChatRoomMemberRepository.readIdIfExist(userId, friendId)
    }

    fun readGroupFriend(chatRoomId: String, userId: String): List<ChatRoomMemberInfo> {
        return groupChatRoomMemberRepository.readFriends(chatRoomId, userId)
    }

    fun readPersonalFriend(chatRoomId: String, userId: String): ChatRoomMemberInfo {
        return personalChatRoomMemberRepository.readFriend(chatRoomId, userId) ?: throw NotFoundException(ErrorCode.CHATROOM_NOT_FOUND)
    }
    fun readOwnedChatRoomMembers(userId: String): List<ChatRoomMemberInfo> {
        val groupChatRoomMembers = groupChatRoomMemberRepository.reads(userId)
        val personalChatRoomMember = personalChatRoomMemberRepository.reads(userId)
        return groupChatRoomMembers + personalChatRoomMember
    }
}
