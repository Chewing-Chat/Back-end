package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.DirectChatRoomMemberRepository
import org.chewing.v1.repository.chat.DirectChatRoomMemberSequenceRepository
import org.chewing.v1.repository.chat.DirectChatRoomRepository
import org.springframework.stereotype.Component

@Component
class DirectChatRoomReader(
    private val directChatRoomRepository: DirectChatRoomRepository,
    private val directChatRoomMemberRepository: DirectChatRoomMemberRepository,
    private val directChatRoomMemberSequenceRepository: DirectChatRoomMemberSequenceRepository,
) {
    fun readRoomInfo(chatRoomId: ChatRoomId, userId: UserId) =
        directChatRoomRepository.readInfo(chatRoomId, userId) ?: throw NotFoundException(
            ErrorCode.CHATROOM_NOT_FOUND,
        )

    fun readRoomInfoByRelation(userId: UserId, friendId: UserId) =
        directChatRoomRepository.readWithRelation(userId, friendId)

    fun readMemberInfo(userId: UserId, chatRoomId: ChatRoomId) =
        directChatRoomMemberRepository.readInfo(userId, chatRoomId)

    fun readRoomSequence(chatRoomId: ChatRoomId, userId: UserId) =
        directChatRoomMemberSequenceRepository.readSequence(chatRoomId, userId)

    fun readsMemberInfos(chatRoomIds: List<ChatRoomId>, userId: UserId) =
        directChatRoomMemberRepository.readsMemberInfos(chatRoomIds, userId)

    fun readRoomInfos(userId: UserId) = directChatRoomRepository.readDirectChatRooms(userId)

    fun readsRoomSequence(chatRoomIds: List<ChatRoomId>, userId: UserId) =
        directChatRoomMemberSequenceRepository.readsSequence(chatRoomIds, userId)
}
