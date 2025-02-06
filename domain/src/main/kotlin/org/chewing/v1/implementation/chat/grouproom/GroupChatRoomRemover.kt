package org.chewing.v1.implementation.chat.grouproom

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.chewing.v1.repository.chat.GroupChatRoomRepository
import org.springframework.stereotype.Component

@Component
class GroupChatRoomRemover(
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository
) {
    fun removeMember(chatRoomId: ChatRoomId, userId: UserId) {
        groupChatRoomMemberRepository.remove(chatRoomId, userId)
    }
}
