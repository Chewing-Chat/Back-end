package org.chewing.v1.implementation.chat.grouproom

import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.GroupChatRoom
import org.chewing.v1.model.chat.room.GroupChatRoomInfo
import org.chewing.v1.model.chat.room.GroupChatRoomMemberInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class GroupChatRoomEnricher {
    fun enrich(
        infos: List<GroupChatRoomInfo>,
        chatRoomSequences: List<ChatRoomSequence>,
        memberSequences: List<ChatRoomMemberSequence>,
        chatRoomMembers: List<GroupChatRoomMemberInfo>,
    ): List<GroupChatRoom> {
        val chatRoomSequenceMap = chatRoomSequences.associateBy { it.chatRoomId }
        val memberSequenceMap = memberSequences.associateBy { it.chatRoomId }
        val chatRoomMemberGroup = chatRoomMembers.groupBy { it.chatRoomId }
        return infos.mapNotNull { chatRoom ->
            val chatRoomSequence = chatRoomSequenceMap[chatRoom.chatRoomId]
            val memberSequence = memberSequenceMap[chatRoom.chatRoomId]
            val members = chatRoomMemberGroup[chatRoom.chatRoomId] ?: emptyList()
            if (chatRoomSequence != null && memberSequence != null) {
                GroupChatRoom.of(chatRoom, members, chatRoomSequence, memberSequence)
            } else {
                null
            }
        }
    }

    fun enrichMember(
        userId: UserId,
        friendIds: List<UserId>,
    ): List<UserId> {
        return friendIds + userId
    }
}
