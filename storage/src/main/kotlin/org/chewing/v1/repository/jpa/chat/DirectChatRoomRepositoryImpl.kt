package org.chewing.v1.repository.jpa.chat

import org.chewing.v1.jpaentity.chat.DirectChatRoomJpaEntity
import org.chewing.v1.jparepository.chat.DirectChatRoomJpaRepository
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.DirectChatRoomInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomRepository
import org.springframework.stereotype.Repository

@Repository
internal class DirectChatRoomRepositoryImpl(
    private val directChatRoomJpaRepository: DirectChatRoomJpaRepository,
) : DirectChatRoomRepository {
    override fun readInfo(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ): DirectChatRoomInfo? {
        val entity = directChatRoomJpaRepository.findById(chatRoomId.id).orElse(null)
        return entity?.toChatRoom(userId)
    }

    override fun append(
        userId: UserId,
        friendId: UserId,
    ): DirectChatRoomInfo {
        DirectChatRoomJpaEntity.generate(
            userId,
            friendId,
        ).let {
            return directChatRoomJpaRepository.save(it).toChatRoom(userId)
        }
    }

    override fun readWithRelation(
        userId: UserId,
        friendId: UserId,
    ): DirectChatRoomInfo? {
        val (aId, bId) = if (userId.id < friendId.id) userId.id to friendId.id else friendId.id to userId.id

        return directChatRoomJpaRepository.findByUserAIdAndUserBId(aId, bId)
            .orElse(null)
            ?.toChatRoom(userId)
    }

    override fun readUsers(userId: UserId): List<DirectChatRoomInfo> {
        return directChatRoomJpaRepository.findByUserId(userId.id).map { it.toChatRoom(userId) }
    }

    override fun remove(
        userId: UserId,
        chatRoomId: ChatRoomId,
    ) {
        directChatRoomJpaRepository.findById(chatRoomId.id).ifPresent { chatRoom ->
            chatRoom.updateStatus(userId, ChatRoomMemberStatus.DELETED)
            directChatRoomJpaRepository.save(chatRoom)
        }
    }

    override fun updateStatus(
        userId: UserId,
        chatRoomId: ChatRoomId,
        status: ChatRoomMemberStatus,
    ) {
        directChatRoomJpaRepository.findById(chatRoomId.id).ifPresent { chatRoom ->
            chatRoom.updateStatus(userId, status)
            directChatRoomJpaRepository.save(chatRoom)
        }
    }
}
