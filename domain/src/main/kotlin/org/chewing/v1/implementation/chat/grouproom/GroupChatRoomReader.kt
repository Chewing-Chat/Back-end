package org.chewing.v1.implementation.chat.grouproom

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
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
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository,
) {
    fun readRoomInfos(chatRoomIds: List<ChatRoomId>): List<GroupChatRoomInfo> {
        return groupChatRoomRepository.readRoomInfos(chatRoomIds)
    }
    fun readsRoomMemberInfos(chatRoomIds: List<ChatRoomId>): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberRepository.readsInfos(chatRoomIds)
    }

    fun readRoomMemberInfos(chatRoomId: ChatRoomId): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberRepository.read(chatRoomId)
    }

    fun readRoomUserInfos(userId: UserId): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberRepository.readUsers(userId)
    }

    fun readRoomInfo(chatRoomId: ChatRoomId): GroupChatRoomInfo {
        return groupChatRoomRepository.readRoomInfo(chatRoomId) ?: throw NotFoundException(ErrorCode.CHATROOM_NOT_FOUND)
    }
}
