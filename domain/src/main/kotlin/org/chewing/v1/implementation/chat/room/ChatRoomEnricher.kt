package org.chewing.v1.implementation.chat.room

import org.chewing.v1.model.chat.member.ChatRoomMember
import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.room.ChatRoomInfo
import org.chewing.v1.model.chat.room.Room
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class ChatRoomEnricher {

    fun enrichChatRooms(
        chatRoomMemberInfos: List<ChatRoomMemberInfo>,
        chatRoomInfos: List<ChatRoomInfo>,
        userId: UserId,
    ): List<Room> {
        // 모든 기준은 chatRoomId를 기준으로 매핑시켜야 함
        // chatRoomId로 chatRoomMemberInfo를 그룹화
        val groupedChatRoomMembers = chatRoomMemberInfos.groupBy { it.chatRoomId }

        return chatRoomInfos.mapNotNull { chatRoomInfo ->
            val myChatRoom = groupedChatRoomMembers[chatRoomInfo.chatRoomId]?.find { it.memberId == userId }
                ?: return@mapNotNull null

            val chatRoomMembersInfo = groupedChatRoomMembers[chatRoomInfo.chatRoomId] ?: emptyList()
            val chatRoomMembers = chatRoomMembersInfo.map {
                ChatRoomMember.of(it.memberId, it.readSeqNumber)
            }
            Room.of(
                chatRoomInfo = chatRoomInfo,
                userChatRoom = myChatRoom,
                chatRoomMembers = chatRoomMembers,
            )
        }
    }
}
