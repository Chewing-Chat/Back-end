package org.chewing.v1.implementation.chat.room

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.user.UserId
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

    fun appendGroupMembers(chatRoomId: String, userIds: List<UserId>, number: ChatLogSequence) {
        groupChatRoomMemberRepository.appends(chatRoomId, userIds, number)
    }

    fun appendIfNotExistPersonalMember(chatRoomId: String, userId: UserId, friendId: UserId, number: ChatLogSequence) {
        personalChatRoomMemberRepository.appendIfNotExist(chatRoomId, userId, friendId, number)
    }

    fun appendInviteMember(chatRoomId: String, userId: UserId, number: ChatLogSequence) {
        groupChatRoomMemberRepository.append(chatRoomId, userId, number)
    }
}
