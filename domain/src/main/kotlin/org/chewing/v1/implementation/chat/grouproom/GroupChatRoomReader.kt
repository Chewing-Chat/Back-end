package org.chewing.v1.implementation.chat.grouproom

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.GroupChatRoomInfo
import org.chewing.v1.model.chat.room.GroupChatRoomMemberInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.chewing.v1.repository.chat.GroupChatRoomRepository
import org.springframework.stereotype.Repository

@Repository
class GroupChatRoomReader(
    private val groupChatRoomRepository: GroupChatRoomRepository,
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository
) {
    fun readRoomInfos(chatRoomIds : List<ChatRoomId>): List<GroupChatRoomInfo> {
        return groupChatRoomRepository.readRoomInfos(chatRoomIds)
    }
    fun readRoomMemberInfos(chatRoomIds: List<ChatRoomId>): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberRepository.readsInfos(chatRoomIds)
    }

    fun readRoomUserInfos(userId: UserId): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberRepository.readUsers(userId)
    }
}
