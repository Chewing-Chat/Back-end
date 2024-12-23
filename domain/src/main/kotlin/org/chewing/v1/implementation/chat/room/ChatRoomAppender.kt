package org.chewing.v1.implementation.chat.room

import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.repository.chat.ChatRoomRepository
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.chewing.v1.repository.chat.PersonalChatRoomMemberRepository
import org.springframework.stereotype.Component

@Component
class ChatRoomAppender(
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository,
    private val personalChatRoomMemberRepository: PersonalChatRoomMemberRepository,
    private val chatRoomRepository: ChatRoomRepository,
) {
    fun append(isGroup: Boolean): String {
        return chatRoomRepository.appendChatRoom(isGroup)
    }

    fun appendGroupMembers(chatRoomId: String, userIds: List<String>, number: ChatNumber) {
        groupChatRoomMemberRepository.appends(chatRoomId, userIds, number)
    }

    fun appendIfNotExistPersonalMember(chatRoomId: String, userId: String, friendId: String, number: ChatNumber) {
        personalChatRoomMemberRepository.appendIfNotExist(chatRoomId, userId, friendId, number)
    }

    fun appendInviteMember(chatRoomId: String, userId: String, number: ChatNumber) {
        groupChatRoomMemberRepository.append(chatRoomId, userId, number)
    }
}
