package org.chewing.v1.external

import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import org.chewing.v1.TestDataFactory
import org.chewing.v1.dto.ChatMessageDto
import org.junit.jupiter.api.Test
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.time.format.DateTimeFormatter

class ExternalChatNotificationClientTest {
    private val messagingTemplate: SimpMessagingTemplate = mockk()
    private val externalChatNotificationClientImpl: ExternalChatNotificationClientImpl = ExternalChatNotificationClientImpl(messagingTemplate)

    @Test
    fun `채팅 메시지 전송 - NormalMessage`() {
        val messageSlot = slot<ChatMessageDto.Normal>()
        val userId = "userId"
        val message = TestDataFactory.createNormalMessage(
            messageId = "messageId",
            chatRoomId = "chatRoomId",
        )
        val formattedTime = message.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))

        justRun { messagingTemplate.convertAndSendToUser(userId, "/queue/chat", capture(messageSlot)) }

        externalChatNotificationClientImpl.sendMessage(message, userId)

        assert(messageSlot.isCaptured)
        assert(messageSlot.captured.messageId == message.messageId)
        assert(messageSlot.captured.chatRoomId == message.chatRoomId)
        assert(messageSlot.captured.text == message.text)
        assert(messageSlot.captured.senderId == message.senderId)
        assert(messageSlot.captured.page == message.number.page)
        assert(messageSlot.captured.seqNumber == message.number.sequenceNumber)
        assert(messageSlot.captured.timestamp == formattedTime)
        assert(messageSlot.captured.type == message.type.name.lowercase())
    }

    @Test
    fun `채팅 메시지 전송 - BombMessage`() {
        val messageSlot = slot<ChatMessageDto.Bomb>()
        val userId = "userId"
        val message = TestDataFactory.createBombMessage(
            messageId = "messageId",
            chatRoomId = "chatRoomId",
        )
        val formattedTime = message.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
        val formattedExpiredTime = message.expiredAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))

        justRun { messagingTemplate.convertAndSendToUser(userId, "/queue/chat", capture(messageSlot)) }

        externalChatNotificationClientImpl.sendMessage(message, userId)

        assert(messageSlot.isCaptured)
        assert(messageSlot.captured.messageId == message.messageId)
        assert(messageSlot.captured.chatRoomId == message.chatRoomId)
        assert(messageSlot.captured.text == message.text)
        assert(messageSlot.captured.senderId == message.senderId)
        assert(messageSlot.captured.page == message.number.page)
        assert(messageSlot.captured.seqNumber == message.number.sequenceNumber)
        assert(messageSlot.captured.timestamp == formattedTime)
        assert(messageSlot.captured.type == message.type.name.lowercase())
        assert(messageSlot.captured.expiredAt == formattedExpiredTime)
    }

    @Test
    fun `채팅 메시지 전송 - ReplyMessage`() {
        val messageSlot = slot<ChatMessageDto.Reply>()
        val userId = "userId"
        val message = TestDataFactory.createReplyMessage(
            messageId = "messageId",
            chatRoomId = "chatRoomId",
        )
        val formattedTime = message.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))

        justRun { messagingTemplate.convertAndSendToUser(userId, "/queue/chat", capture(messageSlot)) }

        externalChatNotificationClientImpl.sendMessage(message, userId)

        assert(messageSlot.isCaptured)
        assert(messageSlot.captured.messageId == message.messageId)
        assert(messageSlot.captured.chatRoomId == message.chatRoomId)
        assert(messageSlot.captured.text == message.text)
        assert(messageSlot.captured.senderId == message.senderId)
        assert(messageSlot.captured.page == message.number.page)
        assert(messageSlot.captured.seqNumber == message.number.sequenceNumber)
        assert(messageSlot.captured.timestamp == formattedTime)
        assert(messageSlot.captured.type == message.type.name.lowercase())
        assert(messageSlot.captured.parentMessageId == message.parentMessageId)
        assert(messageSlot.captured.parentMessagePage == message.parentMessagePage)
        assert(messageSlot.captured.parentSeqNumber == message.parentSeqNumber)
        assert(messageSlot.captured.parentMessageText == message.parentMessageText)
    }

    @Test
    fun `채팅 메시지 전송 - DeleteMessage`() {
        val messageSlot = slot<ChatMessageDto.Delete>()
        val userId = "userId"
        val message = TestDataFactory.createDeleteMessage(
            targetMessageId = "messageId",
            chatRoomId = "chatRoomId",
        )
        val formattedTime = message.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))

        justRun { messagingTemplate.convertAndSendToUser(userId, "/queue/chat", capture(messageSlot)) }

        externalChatNotificationClientImpl.sendMessage(message, userId)

        assert(messageSlot.isCaptured)
        assert(messageSlot.captured.targetMessageId == message.targetMessageId)
        assert(messageSlot.captured.chatRoomId == message.chatRoomId)
        assert(messageSlot.captured.senderId == message.senderId)
        assert(messageSlot.captured.page == message.number.page)
        assert(messageSlot.captured.seqNumber == message.number.sequenceNumber)
        assert(messageSlot.captured.timestamp == formattedTime)
        assert(messageSlot.captured.type == message.type.name.lowercase())
    }

    @Test
    fun `채팅 메시지 전송 - LeaveMessage`() {
        val messageSlot = slot<ChatMessageDto.Leave>()
        val userId = "userId"
        val message = TestDataFactory.createLeaveMessage(
            messageId = "messageId",
            chatRoomId = "chatRoomId",
        )
        val formattedTime = message.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))

        justRun { messagingTemplate.convertAndSendToUser(userId, "/queue/chat", capture(messageSlot)) }

        externalChatNotificationClientImpl.sendMessage(message, userId)

        assert(messageSlot.isCaptured)
        assert(messageSlot.captured.messageId == message.messageId)
        assert(messageSlot.captured.chatRoomId == message.chatRoomId)
        assert(messageSlot.captured.senderId == message.senderId)
        assert(messageSlot.captured.page == message.number.page)
        assert(messageSlot.captured.seqNumber == message.number.sequenceNumber)
        assert(messageSlot.captured.timestamp == formattedTime)
        assert(messageSlot.captured.type == message.type.name.lowercase())
    }

    @Test
    fun `채팅 메시지 전송 - InviteMessage`() {
        val messageSlot = slot<ChatMessageDto.Invite>()
        val userId = "userId"
        val message = TestDataFactory.createInviteMessage(
            messageId = "messageId",
            chatRoomId = "chatRoomId",
        )
        val formattedTime = message.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))

        justRun { messagingTemplate.convertAndSendToUser(userId, "/queue/chat", capture(messageSlot)) }

        externalChatNotificationClientImpl.sendMessage(message, userId)

        assert(messageSlot.isCaptured)
        assert(messageSlot.captured.messageId == message.messageId)
        assert(messageSlot.captured.chatRoomId == message.chatRoomId)
        assert(messageSlot.captured.senderId == message.senderId)
        assert(messageSlot.captured.page == message.number.page)
        assert(messageSlot.captured.seqNumber == message.number.sequenceNumber)
        assert(messageSlot.captured.timestamp == formattedTime)
        assert(messageSlot.captured.type == message.type.name.lowercase())
    }

    @Test
    fun `채팅 메시지 전송 - ReadMessage`() {
        val messageSlot = slot<ChatMessageDto.Read>()
        val userId = "userId"
        val message = TestDataFactory.createReadMessage(
            chatRoomId = "chatRoomId",
        )
        val formattedTime = message.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))

        justRun { messagingTemplate.convertAndSendToUser(userId, "/queue/chat", capture(messageSlot)) }

        externalChatNotificationClientImpl.sendMessage(message, userId)

        assert(messageSlot.isCaptured)
        assert(messageSlot.captured.chatRoomId == message.chatRoomId)
        assert(messageSlot.captured.senderId == message.senderId)
        assert(messageSlot.captured.page == message.number.page)
        assert(messageSlot.captured.seqNumber == message.number.sequenceNumber)
        assert(messageSlot.captured.timestamp == formattedTime)
        assert(messageSlot.captured.type == message.type.name.lowercase())
    }

    @Test
    fun `채팅 메시지 전송 - FileMessage`() {
        val messageSlot = slot<ChatMessageDto.File>()
        val userId = "userId"
        val message = TestDataFactory.createFileMessage(
            messageId = "messageId",
            chatRoomId = "chatRoomId",
        )
        val formattedTime = message.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))

        justRun { messagingTemplate.convertAndSendToUser(userId, "/queue/chat", capture(messageSlot)) }

        externalChatNotificationClientImpl.sendMessage(message, userId)

        assert(messageSlot.isCaptured)
        assert(messageSlot.captured.messageId == message.messageId)
        assert(messageSlot.captured.chatRoomId == message.chatRoomId)
        assert(messageSlot.captured.senderId == message.senderId)
        assert(messageSlot.captured.page == message.number.page)
        assert(messageSlot.captured.seqNumber == message.number.sequenceNumber)
        assert(messageSlot.captured.timestamp == formattedTime)
        assert(messageSlot.captured.type == message.type.name.lowercase())
        assert(messageSlot.captured.files[0].fileUrl == message.medias[0].url)
        println(messageSlot.captured.files[0].fileType)
        assert(messageSlot.captured.files[0].fileType == message.medias[0].type.value().lowercase())
        assert(messageSlot.captured.files[0].index == message.medias[0].index)
    }
}
