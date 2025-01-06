package org.chewing.v1.repository

import org.chewing.v1.config.MongoContextTest
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.chat.log.*
import org.chewing.v1.mongorepository.ChatLogMongoRepository
import org.chewing.v1.repository.mongo.chat.ChatLogRepositoryImpl
import org.chewing.v1.repository.support.ChatMessageProvider
import org.chewing.v1.repository.support.MongoDataGenerator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.temporal.ChronoUnit
import java.util.UUID

class ChatLogRepositoryTest : MongoContextTest() {
    @Autowired
    private lateinit var chatLogMongoRepository: ChatLogMongoRepository

    @Autowired
    private lateinit var mongoDataGenerator: MongoDataGenerator

    private val chatLogRepositoryImpl: ChatLogRepositoryImpl by lazy {
        ChatLogRepositoryImpl(chatLogMongoRepository)
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
        val chatMessage = ChatMessageProvider.buildNormalMessage(messageId, chatRoomId)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatNormalLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.text == chatMessage.text)
        assert(chatLog.number.sequenceNumber == chatMessage.number.sequenceNumber)
        assert(chatLog.number.page == chatMessage.number.page)
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
        val chatMessage = ChatMessageProvider.buildLeaveMessage(messageId, chatRoomId)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatLeaveLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.number.sequenceNumber == chatMessage.number.sequenceNumber)
        assert(chatLog.number.page == chatMessage.number.page)
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
        val chatMessage = ChatMessageProvider.buildInviteMessage(messageId, chatRoomId)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatInviteLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.number.sequenceNumber == chatMessage.number.sequenceNumber)
        assert(chatLog.number.page == chatMessage.number.page)
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
        val chatMessage = ChatMessageProvider.buildFileMessage(messageId, chatRoomId)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatFileLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.number.sequenceNumber == chatMessage.number.sequenceNumber)
        assert(chatLog.number.page == chatMessage.number.page)
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
        val parentLog = ChatMessageProvider.buildNormalLog(parentMessageId, chatRoomId)
        val chatMessage = ChatMessageProvider.buildReplyMessage(messageId, chatRoomId, parentLog)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatReplyLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.number.sequenceNumber == chatMessage.number.sequenceNumber)
        assert(chatLog.number.page == chatMessage.number.page)
        assert(
            chatLog.timestamp.truncatedTo(ChronoUnit.MILLIS)
                .equals(chatMessage.timestamp.truncatedTo(ChronoUnit.MILLIS)),
        )
        assert(chatLog.text == chatMessage.text)
        assert(chatLog.type == ChatLogType.REPLY)
        assert(chatLog.parentMessageId == parentMessageId)
        assert(chatLog.parentMessagePage == parentLog.number.page)
        assert(chatLog.parentMessageText == parentLog.text)
        assert(chatLog.parentSeqNumber == parentLog.number.sequenceNumber)
        assert(chatLog.parentMessageType == parentLog.type)
    }

    @Test
    fun `폭탄 채팅 읽기 - 존재함`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildBombMessage(messageId, chatRoomId)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatBombLog
        assert(chatLog.messageId == messageId)
        assert(chatLog.chatRoomId == chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.text == chatMessage.text)
        assert(chatLog.number.sequenceNumber == chatMessage.number.sequenceNumber)
        assert(chatLog.number.page == chatMessage.number.page)
        assert(
            chatLog.timestamp.truncatedTo(ChronoUnit.MILLIS)
                .equals(chatMessage.timestamp.truncatedTo(ChronoUnit.MILLIS)),
        )
        assert(chatLog.type == ChatLogType.BOMB)
        assert(
            chatLog.expiredAt.truncatedTo(ChronoUnit.MILLIS)
                .equals(chatMessage.expiredAt.truncatedTo(ChronoUnit.MILLIS)),
        )
    }

    @Test
    fun `채팅로그 리스트 읽기`() {
        val chatRoomId = generateChatRoomId()
        val chatNormalMessage = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId)
        val chatLeaveMessage = ChatMessageProvider.buildLeaveMessage(generateMessageId(), chatRoomId)
        val chatInviteMessage = ChatMessageProvider.buildInviteMessage(generateMessageId(), chatRoomId)
        val chatFileMessage = ChatMessageProvider.buildFileMessage(generateMessageId(), chatRoomId)
        val chatReplyMessage = ChatMessageProvider.buildReplyMessage(
            generateMessageId(),
            chatRoomId,
            ChatMessageProvider.buildNormalLog(generateMessageId(), chatRoomId),
        )
        val chatBombeMessage = ChatMessageProvider.buildBombMessage(generateMessageId(), chatRoomId)
        mongoDataGenerator.chatLogEntityData(chatNormalMessage)
        mongoDataGenerator.chatLogEntityData(chatLeaveMessage)
        mongoDataGenerator.chatLogEntityData(chatInviteMessage)
        mongoDataGenerator.chatLogEntityData(chatFileMessage)
        mongoDataGenerator.chatLogEntityData(chatReplyMessage)
        mongoDataGenerator.chatLogEntityData(chatBombeMessage)
        val chatLogs = chatLogRepositoryImpl.readChatMessages(chatRoomId, 1)
        assert(chatLogs.size == 6)
        assert(chatLogs[0] is ChatNormalLog)
        assert(chatLogs[1] is ChatLeaveLog)
        assert(chatLogs[2] is ChatInviteLog)
        assert(chatLogs[3] is ChatFileLog)
        assert(chatLogs[4] is ChatReplyLog)
        assert(chatLogs[5] is ChatBombLog)
    }

    @Test
    fun `일반 채팅로그 삭제`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildNormalMessage(messageId, chatRoomId)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        chatLogRepositoryImpl.removeLog(messageId)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId)
        assert(chatLog!!.type == ChatLogType.DELETE)
    }

    @Test
    fun `채팅방 나감 채팅로그 삭제`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildLeaveMessage(messageId, chatRoomId)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        chatLogRepositoryImpl.removeLog(messageId)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId)
        assert(chatLog!!.type == ChatLogType.DELETE)
    }

    @Test
    fun `채팅방 초대 채팅로그 삭제`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildInviteMessage(messageId, chatRoomId)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        chatLogRepositoryImpl.removeLog(messageId)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId)
        assert(chatLog!!.type == ChatLogType.DELETE)
    }

    @Test
    fun `파일 채팅로그 삭제`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildFileMessage(messageId, chatRoomId)
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
        val parentLog = ChatMessageProvider.buildNormalLog(parentMessageId, chatRoomId)
        val chatMessage = ChatMessageProvider.buildReplyMessage(messageId, chatRoomId, parentLog)
        mongoDataGenerator.chatLogEntityData(chatMessage)
        chatLogRepositoryImpl.removeLog(messageId)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId)
        assert(chatLog!!.type == ChatLogType.DELETE)
    }

    @Test
    fun `채팅로그 추가`() {
        val messageId = generateMessageId()
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildNormalMessage(messageId, chatRoomId)
        chatLogRepositoryImpl.appendChatLog(chatMessage)
        val chatLog = chatLogRepositoryImpl.readChatMessage(messageId) as ChatNormalLog
        assert(chatLog.messageId == chatMessage.messageId)
        assert(chatLog.chatRoomId == chatMessage.chatRoomId)
        assert(chatLog.senderId == chatMessage.senderId)
        assert(chatLog.text == chatMessage.text)
        assert(chatLog.number.sequenceNumber == chatMessage.number.sequenceNumber)
        assert(chatLog.number.page == chatMessage.number.page)
        assert(
            chatLog.timestamp.truncatedTo(ChronoUnit.MILLIS)
                .equals(chatMessage.timestamp.truncatedTo(ChronoUnit.MILLIS)),
        )
        assert(chatLog.type == ChatLogType.NORMAL)
    }

    @Test
    fun `잘못된 타입의 메시지  저장시 에러 발생 - 읽기 메시지`() {
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildReadMessage(chatRoomId)

        val exception = assertThrows<ConflictException> {
            chatLogRepositoryImpl.appendChatLog(chatMessage)
        }
        assert(exception.errorCode == ErrorCode.INVALID_TYPE)
    }

    @Test
    fun `잘못된 타입의 메시지  저장시 에러 발생 - 삭제 메시지`() {
        val chatRoomId = generateChatRoomId()
        val chatMessage = ChatMessageProvider.buildDeleteMessage(chatRoomId)

        val exception = assertThrows<ConflictException> {
            chatLogRepositoryImpl.appendChatLog(chatMessage)
        }
        assert(exception.errorCode == ErrorCode.INVALID_TYPE)
    }

    @Test
    fun `마지막 메시지 조회`() {
        val chatMessage1 = ChatMessageProvider.buildNormalMessage(generateMessageId(), generateChatRoomId())
        val chatMessage2 = ChatMessageProvider.buildNormalMessage(generateMessageId(), generateChatRoomId())
        val chatMessage3 = ChatMessageProvider.buildNormalMessage(generateMessageId(), generateChatRoomId())
        mongoDataGenerator.chatLogEntityData(chatMessage1)
        mongoDataGenerator.chatLogEntityData(chatMessage2)
        mongoDataGenerator.chatLogEntityData(chatMessage3)
        val chatLog = chatLogRepositoryImpl.readLatestMessages(
            listOf(
                chatMessage1.number,
                chatMessage2.number,
                chatMessage3.number,
            ),
        )
        assert(chatLog.size == 3)
    }

    @Test
    fun `키워드로 채팅 조회`() {
        val chatRoomId = generateChatRoomId()
        val chatMessage1 = ChatMessageProvider.buildNormalMessageForSearch(generateMessageId(), chatRoomId, "밥먹자")
        val chatMessage2 = ChatMessageProvider.buildNormalMessageForSearch(generateMessageId(), chatRoomId, "언제 밥 먹을 건데?")
        val chatMessage3 = ChatMessageProvider.buildNormalMessageForSearch(generateMessageId(), chatRoomId, "지금 밥 먹을 거야")
        val keyword = "밥, 먹자, 언제, 지금"
        mongoDataGenerator.chatLogEntityData(chatMessage1)
        mongoDataGenerator.chatLogEntityData(chatMessage2)
        mongoDataGenerator.chatLogEntityData(chatMessage3)
        val chatLog = chatLogRepositoryImpl.readChatKeyWordMessages(chatMessage1.chatRoomId, keyword)
        assert(chatLog.size == 3)
    }

    private fun generateChatRoomId(): String = UUID.randomUUID().toString()

    private fun generateMessageId(): String = UUID.randomUUID().toString()
}
