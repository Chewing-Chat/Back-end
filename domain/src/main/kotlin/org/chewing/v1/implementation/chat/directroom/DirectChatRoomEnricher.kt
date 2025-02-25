package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.springframework.stereotype.Component

@Component
class DirectChatRoomEnricher {
    fun enrich(
        infos: List<DirectChatRoomInfo>,
        chatRoomSequences: List<ChatRoomSequence>,
        memberSequences: List<ChatRoomMemberSequence>
    ): List<DirectChatRoom> {
        val chatRoomSequenceMap = chatRoomSequences.associateBy { it.chatRoomId }
        val memberSequenceMap = memberSequences.associateBy { it.chatRoomId }

        return infos.mapNotNull { chatRoom ->
            val chatRoomSequence = chatRoomSequenceMap[chatRoom.chatRoomId]
            val memberSequence = memberSequenceMap[chatRoom.chatRoomId]

            if (chatRoomSequence != null && memberSequence != null) {
                DirectChatRoom.of(chatRoom, chatRoomSequence, memberSequence)
            } else {
                null
            }
        }
    }
    fun enrichUnRead(
        infos: List<DirectChatRoomInfo>,
        chatRoomSequences: List<ChatRoomSequence>,
        memberSequences: List<ChatRoomMemberSequence>
    ): List<DirectChatRoom> {
        val chatRoomSequenceMap = chatRoomSequences.associateBy { it.chatRoomId }
        val memberSequenceMap = memberSequences.associateBy { it.chatRoomId }

        return infos.mapNotNull { chatRoom ->
            val chatRoomSequence = chatRoomSequenceMap[chatRoom.chatRoomId]
            val memberSequence = memberSequenceMap[chatRoom.chatRoomId]

            if (chatRoomSequence != null && memberSequence != null && memberSequence.readSequenceNumber < chatRoomSequence.sequence) {
                DirectChatRoom.of(chatRoom, chatRoomSequence, memberSequence)
            } else {
                null
            }
        }
    }
}
