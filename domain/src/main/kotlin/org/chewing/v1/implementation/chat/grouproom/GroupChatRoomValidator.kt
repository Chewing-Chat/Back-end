package org.chewing.v1.implementation.chat.grouproom

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.springframework.stereotype.Component

@Component
class GroupChatRoomValidator(
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository,
) {
    fun isParticipant(chatRoomId: ChatRoomId, userId: UserId) {
        if (!groupChatRoomMemberRepository.checkParticipant(chatRoomId, userId)) {
            throw ConflictException(ErrorCode.CHATROOM_NOT_PARTICIPANT)
        }
    }
}
