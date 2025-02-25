package org.chewing.v1.implementation.chat.grouproom

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.chewing.v1.repository.chat.GroupChatRoomRepository
import org.springframework.stereotype.Component

@Component
class GroupChatRoomAppender(
    private val groupChatRoomRepository: GroupChatRoomRepository,
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository,
) {
    fun appendRoom(groupName: String): ChatRoomId {
        return groupChatRoomRepository.append(groupName)
    }

    fun appendMembers(chatRoomId: ChatRoomId, userIds: List<UserId>) {
        userIds.map { userId ->
            groupChatRoomMemberRepository.append(chatRoomId, userId)
        }
    }

    fun appendMember(chatRoomId: ChatRoomId, userId: UserId) {
        return groupChatRoomMemberRepository.append(chatRoomId, userId)
    }
}
