package org.chewing.v1.repository.jpa.chat

import org.chewing.v1.jpaentity.chat.ChatRoomMemberId
import org.chewing.v1.jpaentity.chat.GroupChatRoomMemberJpaEntity
import org.chewing.v1.jparepository.chat.GroupChatRoomMemberJpaRepository
import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class GroupChatRoomMemberRepositoryImpl(
    private val groupChatRoomMemberJpaRepository: GroupChatRoomMemberJpaRepository,
) : GroupChatRoomMemberRepository {

    override fun readFriends(chatRoomId: String, userId: String): List<ChatRoomMemberInfo> = groupChatRoomMemberJpaRepository.findAllByIdChatRoomIdAndIdUserIdNot(chatRoomId, userId).map {
        it.toRoomMember()
    }

    override fun reads(userId: String): List<ChatRoomMemberInfo> {
        // Step 1: 해당 유저가 속한 채팅방 ID 목록을 조회
        val userChatRooms = groupChatRoomMemberJpaRepository.findAllByIdUserId(userId)

        // chatRoomId 목록만 추출
        val chatRoomIds = userChatRooms.map { it.chatRoomId() }

        // Step 2: 해당 chatRoomIds에 속한 모든 멤버를 조회
        return groupChatRoomMemberJpaRepository.findByIdChatRoomIdIn(chatRoomIds).map { it.toRoomMember() }
    }

    override fun updateFavorite(chatRoomId: String, userId: String, isFavorite: Boolean) {
        groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId(chatRoomId, userId)).ifPresent {
            it.updateFavorite(isFavorite)
            groupChatRoomMemberJpaRepository.save(it)
        }
    }

    @Transactional
    override fun removes(chatRoomIds: List<String>, userId: String) {
        val chatRoomMemberIds = chatRoomIds.map { ChatRoomMemberId(it, userId) }
        groupChatRoomMemberJpaRepository.deleteAllById(chatRoomMemberIds)
    }

    override fun appends(chatRoomId: String, userIds: List<String>, number: ChatNumber) {
        userIds.map { GroupChatRoomMemberJpaEntity.generate(it, chatRoomId, number) }.let {
            groupChatRoomMemberJpaRepository.saveAll(it)
        }
    }

    override fun updateRead(userId: String, number: ChatNumber) {
        groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId(number.chatRoomId, userId)).ifPresent {
            it.updateRead(number)
            groupChatRoomMemberJpaRepository.save(it)
        }
    }

    override fun append(chatRoomId: String, userId: String, number: ChatNumber) {
        groupChatRoomMemberJpaRepository.save(GroupChatRoomMemberJpaEntity.generate(userId, chatRoomId, number))
    }
}
