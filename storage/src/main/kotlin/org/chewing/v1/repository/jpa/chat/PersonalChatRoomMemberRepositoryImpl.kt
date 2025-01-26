package org.chewing.v1.repository.jpa.chat

import org.chewing.v1.jpaentity.chat.ChatRoomMemberId
import org.chewing.v1.jpaentity.chat.PersonalChatRoomMemberJpaEntity
import org.chewing.v1.jparepository.chat.PersonalChatRoomMemberJpaRepository
import org.chewing.v1.model.chat.member.ChatRoomMemberInfo
import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.PersonalChatRoomMemberRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class PersonalChatRoomMemberRepositoryImpl(
    private val personalChatRoomMemberJpaRepository: PersonalChatRoomMemberJpaRepository,
) : PersonalChatRoomMemberRepository {
    override fun readFriend(chatRoomId: String, userId: UserId): ChatRoomMemberInfo? =
        personalChatRoomMemberJpaRepository.findById(ChatRoomMemberId.of(chatRoomId, userId)).orElse(null)
            ?.toRoomFriend()

    override fun readIdIfExist(userId: UserId, friendId: UserId): String? =
        personalChatRoomMemberJpaRepository.findPersonalChatRoomId(userId.id, friendId.id)

    override fun reads(userId: UserId): List<ChatRoomMemberInfo> =
        personalChatRoomMemberJpaRepository.findAllByIdUserId(userId.id).flatMap {
            listOf(it.toRoomOwned(), it.toRoomFriend())
        }

    override fun appendIfNotExist(chatRoomId: String, userId: UserId, friendId: UserId, number: ChatLogSequence) {
        personalChatRoomMemberJpaRepository.findById(ChatRoomMemberId.of(chatRoomId, userId)).orElseGet {
            val entity = PersonalChatRoomMemberJpaEntity.generate(
                userId,
                friendId,
                chatRoomId,
                number,
            )
            personalChatRoomMemberJpaRepository.save(entity)
        }
    }

    override fun updateRead(userId: UserId, number: ChatLogSequence) {
        personalChatRoomMemberJpaRepository.findById(ChatRoomMemberId.of(number.chatRoomId, userId)).ifPresent {
            it.updateRead(number)
            personalChatRoomMemberJpaRepository.save(it)
        }
    }

    override fun updateFavorite(chatRoomId: String, userId: UserId, isFavorite: Boolean) {
        personalChatRoomMemberJpaRepository.findById(ChatRoomMemberId.of(chatRoomId, userId)).ifPresent {
            it.updateFavorite(isFavorite)
            personalChatRoomMemberJpaRepository.save(it)
        }
    }

    @Transactional
    override fun removes(chatRoomIds: List<String>, userId: UserId) {
        val chatRoomMemberIds = chatRoomIds.map { ChatRoomMemberId.of(it, userId) }
        personalChatRoomMemberJpaRepository.deleteAllById(chatRoomMemberIds)
    }
}
