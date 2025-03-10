package org.chewing.v1.repository.jpa.chat

import org.chewing.v1.jpaentity.chat.ChatRoomMemberId
import org.chewing.v1.jpaentity.chat.GroupChatRoomMemberJpaEntity
import org.chewing.v1.jparepository.chat.GroupChatRoomMemberJpaRepository
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.GroupChatRoomMemberInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.springframework.stereotype.Repository

@Repository
internal class GroupChatRoomMemberRepositoryImpl(
    private val groupChatRoomMemberJpaRepository: GroupChatRoomMemberJpaRepository,
) : GroupChatRoomMemberRepository {
    override fun append(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ) {
        val chatRoomMemberId = ChatRoomMemberId.of(chatRoomId, userId)

        groupChatRoomMemberJpaRepository.findById(chatRoomMemberId)
            .map {
                if (it.userStatus == ChatRoomMemberStatus.DELETED) {
                    it.updateStatus(ChatRoomMemberStatus.NORMAL)
                    groupChatRoomMemberJpaRepository.save(it)
                }
                it.toChatRoomMember()
            }
            .orElseGet {
                groupChatRoomMemberJpaRepository.save(GroupChatRoomMemberJpaEntity.generate(chatRoomId, userId))
                    .toChatRoomMember()
            }
    }

    override fun read(chatRoomId: ChatRoomId): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberJpaRepository.findByIdChatRoomIdAndUserStatusNot(
            chatRoomId.id,
            ChatRoomMemberStatus.DELETED,
        )
            .asSequence()
            .map { it.toChatRoomMember() }
            .toList()
    }

    override fun readUsers(userId: UserId): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberJpaRepository.findAllByIdUserIdAndUserStatusNot(
            userId.id,
            ChatRoomMemberStatus.DELETED,
        )
            .asSequence()
            .map { it.toChatRoomMember() }
            .toList()
    }

    override fun remove(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ) {
        groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId.of(chatRoomId, userId)).ifPresent {
            it.updateStatus(ChatRoomMemberStatus.DELETED)
            groupChatRoomMemberJpaRepository.save(it)
        }
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
        return groupChatRoomMemberJpaRepository.existsByIdAndUserStatusNot(
            ChatRoomMemberId.of(chatRoomId, userId),
            ChatRoomMemberStatus.DELETED,
        )
    }

    override fun readsAllInfos(chatRoomIds: List<ChatRoomId>): List<GroupChatRoomMemberInfo> {
        return groupChatRoomMemberJpaRepository.findAllByIdChatRoomIdIn(
            chatRoomIds.map { it.id },
        )
            .asSequence()
            .map { it.toChatRoomMember() }
            .toList()
    }
}
