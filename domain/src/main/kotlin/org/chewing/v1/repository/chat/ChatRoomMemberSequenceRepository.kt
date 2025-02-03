package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId

interface ChatRoomMemberSequenceRepository {
    fun updateReadSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: ChatSequence): ChatSequence
    fun updateJoinSequence(chatRoomId: ChatRoomId, userId: UserId, chatLogSequence: ChatSequence): ChatSequence
    fun readsSequences(chatRoomIds: List<ChatRoomId>, userId: UserId): List<ChatSequence>
    fun readSequences(chatRoomId: ChatRoomId, userId: UserId): ChatSequence?
}
