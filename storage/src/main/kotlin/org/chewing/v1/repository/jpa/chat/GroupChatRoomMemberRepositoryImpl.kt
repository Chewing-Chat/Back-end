package org.chewing.v1.repository.jpa.chat

import org.chewing.v1.jpaentity.chat.ChatRoomMemberId
import org.chewing.v1.jpaentity.chat.GroupChatRoomMemberJpaEntity
import org.chewing.v1.jparepository.chat.GroupChatRoomJpaRepository
import org.chewing.v1.jparepository.chat.GroupChatRoomMemberJpaRepository
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.GroupChatRoomMemberInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.springframework.stereotype.Repository

@Repository
internal class GroupChatRoomMemberRepositoryImpl(
    private val groupChatRoomMemberJpaRepository: GroupChatRoomMemberJpaRepository
): GroupChatRoomMemberRepository {
    override fun append(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ): GroupChatRoomMemberInfo {
        return groupChatRoomMemberJpaRepository.save(GroupChatRoomMemberJpaEntity.generate(chatRoomId, userId)).toChatRoomMember()
    }

    override fun read(chatRoomId: ChatRoomId): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberJpaRepository.findByIdChatRoomId(chatRoomId.id)
            .asSequence()
            .map { it.toChatRoomMember() }
            .toList()
    }

    override fun readUsers(userId: UserId): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberJpaRepository.findAllByIdUserId(userId.id)
            .asSequence()
            .map { it.toChatRoomMember() }
            .toList()
    }

    override fun remove(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ) {
        groupChatRoomMemberJpaRepository.deleteById(ChatRoomMemberId.of(chatRoomId, userId))
    }

    override fun updateStatus(
        chatRoomId: ChatRoomId,
        userId: UserId,
        status: ChatRoomMemberStatus,
    ) {
        val chatRoomMemberId = ChatRoomMemberId.of(chatRoomId, userId)
        groupChatRoomMemberJpaRepository.findById(chatRoomMemberId).ifPresent { chatRoomMember ->
            chatRoomMember.updateStatus(status)
            groupChatRoomMemberJpaRepository.save(chatRoomMember)
        }
    }

    override fun checkParticipant(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ): Boolean {
        return groupChatRoomMemberJpaRepository.existsById(ChatRoomMemberId.of(chatRoomId, userId))
    }

    override fun readsInfos(chatRoomIds: List<ChatRoomId>): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberJpaRepository.findAllByIdChatRoomIdIn(chatRoomIds.map { it.id })
            .asSequence()
            .map { it.toChatRoomMember() }
            .toList()
    }

}
