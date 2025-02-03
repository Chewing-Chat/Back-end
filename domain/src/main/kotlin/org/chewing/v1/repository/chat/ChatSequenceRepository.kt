package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.room.ChatSequenceNumber

interface ChatSequenceRepository {
    // 채팅방의 시퀀스 정보를 조회하는 메서드
    fun readCurrent(chatRoomId: String): ChatSequenceNumber

    // 시퀀스 증가 로직 -> 없다면 생성
    fun updateSequenceIncrement(chatRoomId: String): ChatSequenceNumber
    fun updateSequenceIncrements(chatRoomIds: List<String>): List<ChatSequenceNumber>
    fun readCurrentSeqNumbers(chatRoomIds: List<String>): List<ChatSequenceNumber>
}
