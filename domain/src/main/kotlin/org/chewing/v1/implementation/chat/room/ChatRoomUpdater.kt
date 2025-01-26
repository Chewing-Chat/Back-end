package org.chewing.v1.implementation.chat.room

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.chewing.v1.repository.chat.PersonalChatRoomMemberRepository
import org.springframework.stereotype.Component

@Component
class ChatRoomUpdater(
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository,
    private val personalChatRoomMemberRepository: PersonalChatRoomMemberRepository,
) {
    fun updateFavorite(chatRoomId: String, userId: UserId, isFavorite: Boolean, isGroup: Boolean) {
        isGroup.let {
            if (it) {
                groupChatRoomMemberRepository.updateFavorite(chatRoomId, userId, isFavorite)
            } else {
                personalChatRoomMemberRepository.updateFavorite(chatRoomId, userId, isFavorite)
            }
        }
    }

    fun updateRead(userId: UserId, number: ChatLogSequence, isGroup: Boolean) {
        isGroup.let {
            if (it) {
                groupChatRoomMemberRepository.updateRead(userId, number)
            } else {
                personalChatRoomMemberRepository.updateRead(userId, number)
            }
        }
    }
}
