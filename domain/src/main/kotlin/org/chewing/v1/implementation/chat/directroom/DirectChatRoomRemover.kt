package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.DirectChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomMemberRepository
import org.chewing.v1.repository.chat.DirectChatRoomRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomRemover(
    private val directChatRoomMemberRepository: DirectChatRoomMemberRepository,
) {
    fun removeMember(userId: UserId,chatRoomId: DirectChatRoomId) {
        directChatRoomMemberRepository.remove(userId, chatRoomId)
    }

}
