package org.chewing.v1.implementation.chat.room

import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.chewing.v1.repository.chat.PersonalChatRoomMemberRepository
import org.springframework.stereotype.Component

@Component
class ChatRoomRemover(
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository,
    private val personalChatRoomMemberRepository: PersonalChatRoomMemberRepository,
) {
    fun removeGroups(chatRoomIds: List<String>, userId: String) {
        return groupChatRoomMemberRepository.removes(chatRoomIds, userId)
    }
    fun removePersonals(chatRoomIds: List<String>, userId: String) {
        return personalChatRoomMemberRepository.removes(chatRoomIds, userId)
    }
}
