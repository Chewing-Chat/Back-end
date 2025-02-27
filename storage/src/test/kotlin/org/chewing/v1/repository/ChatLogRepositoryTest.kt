package org.chewing.v1.repository

import org.chewing.v1.config.MongoContextTest
import org.chewing.v1.model.chat.log.*
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.mongorepository.ChatLogMongoRepository
import org.chewing.v1.repository.mongo.chat.log.ChatLogRepositoryImpl
import org.chewing.v1.repository.support.ChatMessageProvider
import org.chewing.v1.repository.support.MongoDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import java.time.temporal.ChronoUnit
import java.util.UUID

class ChatLogRepositoryTest : MongoContextTest() {
    @Autowired
    private lateinit var chatLogMongoRepository: ChatLogMongoRepository

    @Autowired
    private lateinit var mongoDataGenerator: MongoDataGenerator

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private val chatLogRepositoryImpl: ChatLogRepositoryImpl by lazy {
        ChatLogRepositoryImpl(chatLogMongoRepository, mongoTemplate)
    }

    @Test
    fun `채팅 로그 읽기 - 존재하지 않음`() {
        val chatLog = chatLogRepositoryImpl.readChatMessage("testRoomId")
        assert(chatLog == null)
    }

    @Test
    fun `일반 채팅 로그 읽기 - 존재함`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildNormalMessage(messageId, chatRoomId, 1)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatNormalLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.text == chatMessage.text)
        assert(chatLog.roomSequence.sequence == chatMessage.roomSequence.sequence)
        assert(
            chatLog.timestamp.truncatedTo(ChronoUnit.MILLIS)
                .equals(chatMessage.timestamp.truncatedTo(ChronoUnit.MILLIS)),
        )
        assert(chatLog.type == ChatLogType.NORMAL)
    }

    @Test
    fun `채팅방 나감 채팅 로그 읽기 - 존재함`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildLeaveMessage(messageId, chatRoomId, 1)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatLeaveLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.roomSequence.sequence == chatMessage.roomSequence.sequence)
        assert(
            chatLog.timestamp.truncatedTo(ChronoUnit.MILLIS)
                .equals(chatMessage.timestamp.truncatedTo(ChronoUnit.MILLIS)),
        )
        assert(chatLog.type == ChatLogType.LEAVE)
    }

    @Test
    fun `채팅방 초대 채팅 로그 읽기 - 존재함`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildInviteMessage(messageId, chatRoomId, 1)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatInviteLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.roomSequence.sequence == chatMessage.roomSequence.sequence)
        assert(
            chatLog.timestamp.truncatedTo(ChronoUnit.MILLIS)
                .equals(chatMessage.timestamp.truncatedTo(ChronoUnit.MILLIS)),
        )
        assert(chatLog.type == ChatLogType.INVITE)
    }

    @Test
    fun `파일 채팅 로그 읽기 - 존재함`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildFileMessage(messageId, chatRoomId, 1)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatFileLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.roomSequence.sequence == chatMessage.roomSequence.sequence)
        assert(
            chatLog.timestamp.truncatedTo(ChronoUnit.MILLIS)
                .equals(chatMessage.timestamp.truncatedTo(ChronoUnit.MILLIS)),
        )
        assert(chatLog.medias[0].url == chatMessage.medias[0].url)
        assert(chatLog.type == ChatLogType.FILE)
    }

    @Test
    fun `답장 채팅 로그 읽기 - 존재함`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val parentMessageId = generateMessageId()
        val parentLog = ChatMessageProvider.buildNormalLog(parentMessageId, chatRoomId, 1)
        val chatMessage = ChatMessageProvider.buildReplyMessage(messageId, chatRoomId, parentLog, 2)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatReplyLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.roomSequence.sequence == chatMessage.roomSequence.sequence)
        assert(
            chatLog.timestamp.truncatedTo(ChronoUnit.MILLIS)
                .equals(chatMessage.timestamp.truncatedTo(ChronoUnit.MILLIS)),
        )
        assert(chatLog.text == chatMessage.text)
        assert(chatLog.type == ChatLogType.REPLY)
        assert(chatLog.parentMessageId == parentMessageId)
        assert(chatLog.parentMessageText == parentLog.text)
        assert(chatLog.parentSeqNumber == parentLog.roomSequence.sequence)
        assert(chatLog.parentMessageType == parentLog.type)
    }

    @Test
    fun `채팅로그 리스트 읽기 (100개 테스트)`() {
        val chatRoomId = generateChatRoomId()
        // 1부터 100까지의 sequence를 가진 100개의 일반 메시지 생성
        val messages = (1..100).map { seq ->
            ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, seq)
        }

        // 각 메시지를 DB에 저장
        messages.forEach { message ->
            mongoDataGenerator.chatLogEntityData(message)
        }

        val chatLogs = chatLogRepositoryImpl.readChatMessages(chatRoomId, 100, 0)

        // 100개의 메시지가 조회되어야 합니다.
        assert(chatLogs.size == 50)

        // 조회된 메시지의 순서가 1부터 100까지 올바르게 정렬되어 있는지 검증합니다.
        chatLogs.forEachIndexed { index, chatLog ->
            // index는 0부터 시작하므로, 실제 sequence는 index+1
            assert(chatLog.roomSequence.sequence == 100 - index)
        }
    }

    @Test
    fun `채팅로그 리스트 읽기 - 0개`() {
        val chatRoomId = generateChatRoomId()
        val chatNormalMessage = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 5)
        val chatLeaveMessage = ChatMessageProvider.buildLeaveMessage(generateMessageId(), chatRoomId, 4)
        val chatInviteMessage = ChatMessageProvider.buildInviteMessage(generateMessageId(), chatRoomId, 3)
        val chatFileMessage = ChatMessageProvider.buildFileMessage(generateMessageId(), chatRoomId, 2)
        val chatReplyMessage = ChatMessageProvider.buildReplyMessage(
            generateMessageId(),
            chatRoomId,
            ChatMessageProvider.buildNormalLog(generateMessageId(), chatRoomId, 1),
            6,
        )
        mongoDataGenerator.chatLogEntityData(chatNormalMessage)
        mongoDataGenerator.chatLogEntityData(chatLeaveMessage)
        mongoDataGenerator.chatLogEntityData(chatInviteMessage)
        mongoDataGenerator.chatLogEntityData(chatFileMessage)
        mongoDataGenerator.chatLogEntityData(chatReplyMessage)
        val chatLogs = chatLogRepositoryImpl.readChatMessages(chatRoomId, 7, 6)
        assert(chatLogs.isEmpty())
    }

    @Test
    fun `일반 채팅로그 삭제`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildNormalMessage(messageId, chatRoomId, 1)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        chatLogRepositoryImpl.removeLog(messageId)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId)
        assert(chatLog!!.type == ChatLogType.DELETE)
    }

    @Test
    fun `채팅방 나감 채팅로그 삭제`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildLeaveMessage(messageId, chatRoomId, 1)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        chatLogRepositoryImpl.removeLog(messageId)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId)
        assert(chatLog!!.type == ChatLogType.DELETE)
    }

    @Test
    fun `채팅방 초대 채팅로그 삭제`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildInviteMessage(messageId, chatRoomId, 1)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        chatLogRepositoryImpl.removeLog(messageId)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId)
        assert(chatLog!!.type == ChatLogType.DELETE)
    }

    @Test
    fun `파일 채팅로그 삭제`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildFileMessage(messageId, chatRoomId, 1)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        chatLogRepositoryImpl.removeLog(messageId)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId)
        assert(chatLog!!.type == ChatLogType.DELETE)
    }

    @Test
    fun `답장 채팅로그 삭제`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val parentMessageId = generateMessageId()
        val parentLog = ChatMessageProvider.buildNormalLog(parentMessageId, chatRoomId, 1)
        val chatMessage = ChatMessageProvider.buildReplyMessage(messageId, chatRoomId, parentLog, 2)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        chatLogRepositoryImpl.removeLog(messageId)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId)
        assert(chatLog!!.type == ChatLogType.DELETE)
    }

    @Test
    fun `채팅로그 추가`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildNormalMessage(messageId, chatRoomId, 1)
        chatLogRepositoryImpl.appendChatLog(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatNormalLog
        assert(chatLog.messageId == chatMessage.messageId)
        assert(chatLog.chatRoomId == chatMessage.chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.text == chatMessage.text)
        assert(chatLog.roomSequence.sequence == chatMessage.roomSequence.sequence)
        assert(
            chatLog.timestamp.truncatedTo(ChronoUnit.MILLIS)
                .equals(chatMessage.timestamp.truncatedTo(ChronoUnit.MILLIS)),
        )
        assert(chatLog.type == ChatLogType.NORMAL)
    }

    @Test
    fun `채팅방 리스트의 마지막 메시지 조회`() {
        val chatRoomId = generateChatRoomId()
        val chatMessage1 = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 1)
        val chatMessage2 = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 2)
        val chatMessage3 = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 3)
        mongoDataGenerator.chatLogEntityData(chatMessage1)
        mongoDataGenerator.chatLogEntityData(chatMessage2)
        mongoDataGenerator.chatLogEntityData(chatMessage3)
        val chatLog = chatLogRepositoryImpl.readLatestMessages(
            listOf(
                chatRoomId,
            ),
        )
        assert(chatLog.size == 1)

        // 마지막 메시지는 시퀀스가 1인 메시지여야 함
        assert(chatLog[0].roomSequence.sequence == 3)
    }

    @Test
    fun `마지막 메시지 조회`() {
        val chatRoomId = generateChatRoomId()
        val chatMessage1 = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 3)
        val chatMessage2 = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 2)
        val chatMessage3 = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 1)
        mongoDataGenerator.chatLogEntityData(chatMessage1)
        mongoDataGenerator.chatLogEntityData(chatMessage2)
        mongoDataGenerator.chatLogEntityData(chatMessage3)
        val chatLog = chatLogRepositoryImpl.readLatestChatMessage(
            chatRoomId,
        )

        // 마지막 메시지는 시퀀스가 1인 메시지여야 함
        assert(chatLog!!.roomSequence.sequence == 3)
    }

    @Test
    fun `읽지 않은 채팅 로그 조회 - 단일 대상, 읽지 않은 로그 존재`() {
        val chatRoomId = generateChatRoomId()
        // 시퀀스 번호 5인 메시지를 생성 (읽은 시퀀스 4보다 큰 경우)
        val messageId = generateMessageId()
        val chatMessage = ChatMessageProvider.buildNormalMessage(messageId, chatRoomId, 5)
        mongoDataGenerator.chatLogEntityData(chatMessage)

        // UnReadTarget: 읽은 시퀀스가 4이고, 채팅방의 최대 시퀀스가 5인 경우
        val target = UnReadTarget.of(chatRoomId, chatRoomSequence = 5, readSequence = 4)
        val unreadLogs = chatLogRepositoryImpl.readUnreadChatLogs(listOf(target))
        // 해당 메시지가 조회되어야 함
        assert(unreadLogs.size == 1)
        assert(unreadLogs.first().roomSequence.sequence > target.readSequence)
    }

    @Test
    fun `읽지 않은 채팅 로그 조회 - 단일 대상, 읽지 않은 로그 없음`() {
        val chatRoomId = generateChatRoomId()
        // 시퀀스 번호 3인 메시지를 생성
        val messageId = generateMessageId()
        val chatMessage = ChatMessageProvider.buildNormalMessage(messageId, chatRoomId, 3)
        mongoDataGenerator.chatLogEntityData(chatMessage)

        // UnReadTarget: 읽은 시퀀스가 3인 경우, 읽지 않은 로그가 없음
        val target = UnReadTarget.of(chatRoomId, chatRoomSequence = 3, readSequence = 3)
        val unreadLogs = chatLogRepositoryImpl.readUnreadChatLogs(listOf(target))
        // 결과는 빈 리스트여야 함
        assert(unreadLogs.isEmpty())
    }

    @Test
    fun `읽지 않은 채팅 로그 조회 - 다중 대상, 각 채팅방별로 결과 확인`() {
        // 두 개의 채팅방에 대해 각기 다른 조건으로 메시지 생성
        val chatRoomId1 = generateChatRoomId()
        val chatRoomId2 = generateChatRoomId()

        // 채팅방 1: 시퀀스 번호 7인 메시지 생성 (읽은 시퀀스 5보다 큼)
        val messageId1 = generateMessageId()
        val chatMessage1 = ChatMessageProvider.buildNormalMessage(messageId1, chatRoomId1, 7)
        mongoDataGenerator.chatLogEntityData(chatMessage1)

        // 채팅방 2: 시퀀스 번호 4인 메시지 생성 (읽은 시퀀스 3보다 큼)
        val messageId2 = generateMessageId()
        val chatMessage2 = ChatMessageProvider.buildNormalMessage(messageId2, chatRoomId2, 4)
        mongoDataGenerator.chatLogEntityData(chatMessage2)

        // UnReadTarget 설정
        val target1 = UnReadTarget.of(chatRoomId1, chatRoomSequence = 7, readSequence = 5)
        val target2 = UnReadTarget.of(chatRoomId2, chatRoomSequence = 4, readSequence = 3)

        val unreadLogs = chatLogRepositoryImpl.readUnreadChatLogs(listOf(target1, target2))
        // 각 채팅방에서 하나씩 읽지 않은 로그가 조회되어야 함
        assert(unreadLogs.size == 2)
        // 채팅방 1의 경우 확인
        val log1 = unreadLogs.find { it.chatRoomId.id == chatRoomId1.id }
        assert(log1 != null && log1.roomSequence.sequence > target1.readSequence)
        // 채팅방 2의 경우 확인
        val log2 = unreadLogs.find { it.chatRoomId.id == chatRoomId2.id }
        assert(log2 != null && log2.roomSequence.sequence > target2.readSequence)
    }

    @Test
    fun `읽지 않은 채팅 로그 조회 - 다중 대상, 일부 채팅방은 읽음`() {
        val chatRoomId1 = generateChatRoomId()
        val chatRoomId2 = generateChatRoomId()

        // 채팅방 1: 시퀀스 번호 6인 메시지 생성 (읽은 시퀀스 5보다 큼)
        val messageId1 = generateMessageId()
        val chatMessage1 = ChatMessageProvider.buildNormalMessage(messageId1, chatRoomId1, 6)
        mongoDataGenerator.chatLogEntityData(chatMessage1)

        // 채팅방 2: 시퀀스 번호 3인 메시지 생성 (읽은 시퀀스 3과 같음)
        val messageId2 = generateMessageId()
        val chatMessage2 = ChatMessageProvider.buildNormalMessage(messageId2, chatRoomId2, 3)
        mongoDataGenerator.chatLogEntityData(chatMessage2)

        // UnReadTarget 설정: 채팅방 1은 읽지 않은 메시지 존재, 채팅방 2는 없음
        val target1 = UnReadTarget.of(chatRoomId1, chatRoomSequence = 6, readSequence = 5)
        val target2 = UnReadTarget.of(chatRoomId2, chatRoomSequence = 3, readSequence = 3)

        val unreadLogs = chatLogRepositoryImpl.readUnreadChatLogs(listOf(target1, target2))
        // 오직 채팅방 1에서만 읽지 않은 로그가 조회되어야 함
        assert(unreadLogs.size == 1)
        assert(unreadLogs.first().chatRoomId.id == chatRoomId1.id)
    }

    @Test
    fun `키워드로 채팅 로그 읽기 - 해당 메시지 반환`() {
        val chatRoomId = generateChatRoomId()
        val keyword = "hello"

        val messageId1 = generateMessageId()
        val chatMessage1 = ChatMessageProvider.buildNormalMessageWithText(messageId1, chatRoomId, 1, "hello world")
        mongoDataGenerator.chatLogEntityData(chatMessage1)

        val messageId2 = generateMessageId()
        val chatMessage2 = ChatMessageProvider.buildNormalMessageWithText(messageId2, chatRoomId, 2, "goodbye world")
        mongoDataGenerator.chatLogEntityData(chatMessage2)

        val chatLogs = chatLogRepositoryImpl.readChatKeyWordMessages(chatRoomId, keyword)

        assert(chatLogs.size == 1)
        assert(chatLogs.first().messageId == messageId1)
    }

    @Test
    fun `최신 채팅 로그 단건 조회`() {
        // 테스트용 채팅방 생성
        val chatRoomId = generateChatRoomId()
        // 서로 다른 시퀀스 번호를 가진 메시지 세 개 생성
        val chatMessage1 = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 1)
        val chatMessage2 = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 2)
        val chatMessage3 = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 3)

        // 메시지들을 데이터베이스에 저장
        mongoDataGenerator.chatLogEntityData(chatMessage1)
        mongoDataGenerator.chatLogEntityData(chatMessage2)
        mongoDataGenerator.chatLogEntityData(chatMessage3)

        // 최신 메시지 단건 조회 메서드 호출
        val latestChatLog = chatLogRepositoryImpl.readLatestChatMessage(chatRoomId)

        // 검증: 최신 메시지가 chatMessage3여야 함
        assert(latestChatLog != null)
        assert(latestChatLog?.messageId == chatMessage3.messageId)
        assert(latestChatLog?.roomSequence?.sequence == chatMessage3.roomSequence.sequence)
        // 추가 검증: 채팅방 ID 및 송신자 등도 확인할 수 있음
        assert(latestChatLog?.chatRoomId == chatRoomId)
        assert(latestChatLog?.senderId == chatMessage3.senderId)
    }

    private fun generateChatRoomId(): ChatRoomId = ChatRoomId.of(UUID.randomUUID().toString())

    private fun generateMessageId(): String = UUID.randomUUID().toString()
}
