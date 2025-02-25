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
    fun `채팅로그 리스트 읽기`() {
        val chatRoomId = generateChatRoomId()
        val chatNormalMessage = ChatMessageProvider.buildNormalMessage(generateMessageId(), chatRoomId, 5)
        val chatLeaveMessage = ChatMessageProvider.buildLeaveMessage(generateMessageId(), chatRoomId, 4)
        val chatInviteMessage = ChatMessageProvider.buildInviteMessage(generateMessageId(), chatRoomId, 3)
        val chatFileMessage = ChatMessageProvider.buildFileMessage(generateMessageId(), chatRoomId, 2)
        val chatReplyMessage = ChatMessageProvider.buildReplyMessage(
            generateMessageId(),
            chatRoomId,
            ChatMessageProvider.buildNormalLog(generateMessageId(), chatRoomId, 5),
            1,
        )
        val messages = listOf(chatNormalMessage, chatLeaveMessage, chatInviteMessage, chatFileMessage, chatReplyMessage)
        mongoDataGenerator.chatLogEntityData(chatNormalMessage)
        mongoDataGenerator.chatLogEntityData(chatLeaveMessage)
        mongoDataGenerator.chatLogEntityData(chatInviteMessage)
        mongoDataGenerator.chatLogEntityData(chatFileMessage)
        mongoDataGenerator.chatLogEntityData(chatReplyMessage)
        val chatLogs = chatLogRepositoryImpl.readChatMessages(chatRoomId, 5, 0)
        assert(chatLogs.size == messages.size)
        chatLogs.forEachIndexed { index, chatLog ->
            chatLog.roomSequence.sequence == index.plus(1)
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
    fun `마지막 메시지 조회`() {
        val chatMessage1 = ChatMessageProvider.buildNormalMessage(generateMessageId(), generateChatRoomId(), 1)
        val chatMessage2 = ChatMessageProvider.buildNormalMessage(generateMessageId(), generateChatRoomId(), 1)
        val chatMessage3 = ChatMessageProvider.buildNormalMessage(generateMessageId(), generateChatRoomId(), 1)
        mongoDataGenerator.chatLogEntityData(chatMessage1)
        mongoDataGenerator.chatLogEntityData(chatMessage2)
        mongoDataGenerator.chatLogEntityData(chatMessage3)
        val chatLog = chatLogRepositoryImpl.readLatestMessages(
            listOf(
                chatMessage1.chatRoomId,
                chatMessage2.chatRoomId,
                chatMessage3.chatRoomId,
            ),
        )
        assert(chatLog.size == 3)
    }

    private fun generateChatRoomId(): ChatRoomId = ChatRoomId.of(UUID.randomUUID().toString())

    private fun generateMessageId(): String = UUID.randomUUID().toString()
}
