package org.chewing.v1.repository.jpa.chat

import org.chewing.v1.jpaentity.chat.GroupChatRoomJpaEntity
import org.chewing.v1.jparepository.chat.GroupChatRoomJpaRepository
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.GroupChatRoomInfo
import org.chewing.v1.repository.chat.GroupChatRoomRepository
import org.springframework.stereotype.Repository

@Repository
internal class GroupChatRoomRepositoryImpl(
    private val groupChatRoomJpaRepository: GroupChatRoomJpaRepository,
) : GroupChatRoomRepository {
    override fun append(groupName: String): ChatRoomId {
        return groupChatRoomJpaRepository.save(GroupChatRoomJpaEntity.generate(groupName)).toChatRoomId()
    }

    override fun readRoomInfos(chatRoomIds: List<ChatRoomId>): List<GroupChatRoomInfo> {
        return groupChatRoomJpaRepository.findAllById(chatRoomIds.map { it.id })
            .asSequence()
            .map { it.toChatRoom() }
            .toList()
    }

    override fun readRoomInfo(chatRoomId: ChatRoomId): GroupChatRoomInfo? {
        return groupChatRoomJpaRepository.findById(chatRoomId.id)
            .map { it.toChatRoom() }
            .orElse(null)
    }

    override fun updateGroupName(chatRoomId: ChatRoomId, groupName: String) {
        groupChatRoomJpaRepository.findById(chatRoomId.id)
            .map {
                it.updateGroupName(groupName)
                groupChatRoomJpaRepository.save(it)
            }
    }
}
