package org.chewing.v1.repository.jpa.chat

import org.chewing.v1.jpaentity.chat.ChatRoomJpaEntity
import org.chewing.v1.jparepository.chat.ChatRoomJpaRepository
import org.chewing.v1.model.chat.room.ChatRoomInfo
import org.chewing.v1.repository.chat.ChatRoomRepository
import org.springframework.stereotype.Repository

@Repository
/**
 * 채팅방 관련한 메소드만 들어갈게용
 * */
internal class ChatRoomRepositoryImpl(
    private val chatRoomJpaRepository: ChatRoomJpaRepository,

) : ChatRoomRepository {

    override fun appendChatRoom(isGroup: Boolean): String = chatRoomJpaRepository.save(ChatRoomJpaEntity.generate(isGroup)).toChatRoomId()

    override fun readChatRooms(chatRoomIds: List<String>): List<ChatRoomInfo> = chatRoomJpaRepository.findByChatRoomIdIn(chatRoomIds).map { it.toChatRoomInfo() }

    override fun readChatRoom(chatRoomId: String): ChatRoomInfo? = chatRoomJpaRepository.findByChatRoomId(chatRoomId).map { it.toChatRoomInfo() }.orElse(null)

    override fun isGroupChatRoom(chatRoomId: String): Boolean {
        val chatRoom = chatRoomJpaRepository.findByChatRoomId(chatRoomId).orElse(null)
        return chatRoom?.isGroup() ?: false
    }
}
