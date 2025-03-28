package org.chewing.v1.implementation.chat.grouproom

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.chewing.v1.repository.chat.GroupChatRoomRepository
import org.springframework.stereotype.Component

@Component
class GroupChatRoomUpdater(
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository,
    private val groupChatRoomRepository: GroupChatRoomRepository,
) {
    fun updateMemberStatus(chatRoomId: ChatRoomId, userId: UserId, status: ChatRoomMemberStatus) {
        groupChatRoomMemberRepository.updateStatus(chatRoomId, userId, status)
    }

    fun updateGroupName(chatRoomId: ChatRoomId, groupName: String) {
        groupChatRoomRepository.updateGroupName(chatRoomId, groupName)
    }
}
