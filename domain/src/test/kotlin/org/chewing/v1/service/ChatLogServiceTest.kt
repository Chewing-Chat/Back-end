package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.chat.message.ChatAppender
import org.chewing.v1.implementation.chat.message.ChatGenerator
import org.chewing.v1.implementation.chat.message.ChatReader
import org.chewing.v1.implementation.chat.message.ChatRemover
import org.chewing.v1.implementation.chat.message.ChatValidator
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.model.chat.message.MessageType
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.repository.chat.ChatLogRepository
import org.chewing.v1.service.chat.ChatLogService
import org.junit.jupiter.api.Test

class ChatLogServiceTest {
    private val chatLogRepository: ChatLogRepository = mockk()

    private val fileHandler: FileHandler = mockk()
    private val chatAppender: ChatAppender = ChatAppender(chatLogRepository)
    private val chatReader: ChatReader = ChatReader(chatLogRepository)
    private val chatGenerator: ChatGenerator = ChatGenerator()
    private val chatRemover: ChatRemover = ChatRemover(chatLogRepository)
    private val chatValidator: ChatValidator = ChatValidator()

    private val chatLogService: ChatLogService = ChatLogService(
        fileHandler,
        chatAppender,
        chatReader,
        chatGenerator,
        chatRemover,
        chatValidator,
    )

    @Test
    fun `파일 업로드 생성하고 저장해야함`() {
        val fileDataList = listOf(TestDataFactory.createFileData())
        val userId = TestDataFactory.createUserId()
        every {
            fileHandler.handleNewFiles(
                userId,
                fileDataList,
                FileCategory.CHAT,
            )
        } returns listOf(TestDataFactory.createMedia(FileCategory.CHAT, 0, MediaType.IMAGE_PNG))
        val medias = chatLogService.uploadFiles(fileDataList, userId)
        assert(medias.size == 1)
    }

    @Test
    fun `파일 메시지를 생성해야함`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val chatRoomNumber = TestDataFactory.createChatNumber(chatRoomId)
        val medias = listOf(TestDataFactory.createMedia(FileCategory.CHAT, 0, MediaType.IMAGE_PNG))
        val chatRoomType = ChatRoomType.DIRECT
        every {
            chatAppender.appendChatLog(any())
        } just Runs
        val result = chatLogService.mediasMessage(chatRoomId, userId, chatRoomNumber, medias, chatRoomType)
        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        result.medias.forEachIndexed { index, media ->
            assert(media.url == medias[index].url)
            assert(media.category == medias[index].category)
            assert(media.type == medias[index].type)
        }
        assert(result.type == MessageType.FILE)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == chatRoomNumber.sequenceNumber)
        assert(result.chatRoomType == chatRoomType)
    }

    @Test
    fun `읽음 확인 메시지 생성`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val chatRoomNumber = TestDataFactory.createChatNumber(chatRoomId)
        val chatRoomType = ChatRoomType.DIRECT

        val result = chatLogService.readMessage(chatRoomId, userId, chatRoomNumber, chatRoomType)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.type == MessageType.READ)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == chatRoomNumber.sequenceNumber)
        assert(result.chatRoomType == chatRoomType)
    }

    @Test
    fun `일반 메시지 를 삭제 및 기존 메시지 삭제`() {
        val messageId = "messageId"
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val chatRoomType = ChatRoomType.DIRECT
        val parentLog = TestDataFactory.createChatNormalLog(messageId, chatRoomId, userId)

        every { chatReader.readChatMessage(messageId) } returns parentLog
        every { chatLogRepository.removeLog(any()) } just Runs

        val result = chatLogService.deleteMessage(chatRoomId, userId, messageId, chatRoomType)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.targetMessageId == messageId)
        assert(result.type == MessageType.DELETE)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == parentLog.roomSequence.sequenceNumber)
        assert(result.chatRoomType == chatRoomType)
    }

    @Test
    fun `답장 메시지 를 삭제 및 기존 메시지 삭제`() {
        val messageId = "messageId"
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val chatRoomType = ChatRoomType.DIRECT
        val chatRoomNumber = TestDataFactory.createChatNumber(chatRoomId)
        val parentLog = TestDataFactory.createChatReplyLog(messageId, chatRoomId, userId, chatRoomNumber)

        every { chatReader.readChatMessage(messageId) } returns parentLog
        every { chatLogRepository.removeLog(any()) } just Runs

        val result = chatLogService.deleteMessage(chatRoomId, userId, messageId, chatRoomType)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.targetMessageId == messageId)
        assert(result.type == MessageType.DELETE)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == parentLog.roomSequence.sequenceNumber)
        assert(result.chatRoomType == chatRoomType)
    }

    @Test
    fun `파일 메시지 를 삭제 및 기존 메시지 삭제`() {
        val messageId = "messageId"
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val chatRoomType = ChatRoomType.DIRECT
        val chatRoomNumber = TestDataFactory.createChatNumber(chatRoomId)
        val parentLog = TestDataFactory.createChatFileLog(messageId, chatRoomId, userId, chatRoomNumber)

        every { chatReader.readChatMessage(messageId) } returns parentLog
        every { chatLogRepository.removeLog(any()) } just Runs

        val result = chatLogService.deleteMessage(chatRoomId, userId, messageId, chatRoomType)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.targetMessageId == messageId)
        assert(result.type == MessageType.DELETE)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == parentLog.roomSequence.sequenceNumber)
        assert(result.chatRoomType == chatRoomType)
    }

    @Test
    fun `답장 메시지 생성 및 저장 - 일반 메시지`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val parentMessageId = "parentMessageId"
        val text = "text"
        val chatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val parentChatLog =
            TestDataFactory.createChatNormalLog(parentMessageId, chatRoomId, userId)
        val chatRoomType = ChatRoomType.DIRECT

        every { chatLogRepository.readChatMessage(parentMessageId) } returns parentChatLog
        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.replyMessage(chatRoomId, userId, parentMessageId, text, chatNumber, chatRoomType)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.text == text)
        assert(result.type == MessageType.REPLY)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == chatNumber.sequenceNumber)
        assert(result.parentMessageId == parentMessageId)
        assert(result.parentSeqNumber == parentChatLog.roomSequence.sequenceNumber)
        assert(result.parentMessageType == parentChatLog.type)
        assert(result.parentMessageText == parentChatLog.text)
        assert(result.parentMessageId == parentChatLog.messageId)
        assert(result.chatRoomType == chatRoomType)
    }

    @Test
    fun `답장 메시지 생성 및 저장 - 파일 메시지`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val parentMessageId = "parentMessageId"
        val text = "text"
        val chatRoomType = ChatRoomType.DIRECT
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)
        val parentChatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val parentChatLog = TestDataFactory.createChatFileLog(parentMessageId, chatRoomId, userId, parentChatNumber)

        every { chatLogRepository.readChatMessage(parentMessageId) } returns parentChatLog
        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.replyMessage(chatRoomId, userId, parentMessageId, text, seqNumber, chatRoomType)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.text == text)
        assert(result.type == MessageType.REPLY)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.parentMessageId == parentMessageId)
        assert(result.parentSeqNumber == parentChatNumber.sequenceNumber)
        assert(result.parentMessageType == parentChatLog.type)
        assert(result.parentMessageText == parentChatLog.medias[0].url)
        assert(result.parentMessageId == parentChatLog.messageId)
        assert(result.chatRoomType == chatRoomType)
    }

    @Test
    fun `일반 메시지 생성 및 저장`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val text = "text"
        val chatRoomType = ChatRoomType.DIRECT
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.chatNormalMessage(chatRoomId, userId, text, seqNumber, chatRoomType)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.text == text)
        assert(result.type == MessageType.NORMAL)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.chatRoomType == chatRoomType)
    }

    @Test
    fun `채팅방 나가기 메시지 생성 및 저장`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.leaveMessage(chatRoomId, userId, seqNumber, ChatRoomType.DIRECT)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.type == MessageType.LEAVE)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.chatRoomType == ChatRoomType.DIRECT)
    }

    @Test
    fun `초대 메시지 리스트 생성 및 저장`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val friendIds = listOf(TestDataFactory.createUserId(), TestDataFactory.createUserId())
        val userId = TestDataFactory.createUserId()
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.inviteMessages(friendIds, chatRoomId, userId, seqNumber, ChatRoomType.DIRECT)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.type == MessageType.INVITE)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.chatRoomType == ChatRoomType.DIRECT)
        assert(result.targetUserIds.size == 2)
        result.targetUserIds.forEachIndexed { index, targetUserId ->
            assert(targetUserId == friendIds[index])
        }
    }

    @Test
    fun `초대 메시지 생성 및 저장`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val friendId = TestDataFactory.createUserId()
        val userId = TestDataFactory.createUserId()
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.inviteMessage(chatRoomId, friendId, userId, seqNumber, ChatRoomType.DIRECT)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.type == MessageType.INVITE)
        assert(result.roomSequence.chatRoomId == chatRoomId)
        assert(result.roomSequence.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.chatRoomType == ChatRoomType.DIRECT)
        assert(result.targetUserIds.size == 1)
        assert(result.targetUserIds[0] == friendId)
    }

    @Test
    fun `최신 채팅 로그 가져오기`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val chatNormalLog =
            TestDataFactory.createChatNormalLog("messageId", chatRoomId, userId)

        every { chatLogRepository.readLatestMessages(any()) } returns listOf(chatNormalLog)

        val result = chatLogService.getLatestChat(listOf(chatRoomId))

        assert(result.size == 1)

        result.forEachIndexed {
                index, chatLog ->
            assert(chatLog.chatRoomId == chatRoomId)
            assert(chatLog.messageId == chatNormalLog.messageId)
            assert(chatLog.senderId == chatNormalLog.senderId)
        }
    }

    @Test
    fun `채팅 로그 목록 가져오기`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val chatNormalLog = TestDataFactory.createChatNormalLog(
            "messageId",
            chatRoomId,
            userId,
        )
        val joinSequence = 1
        val targetSequence = 1

        every { chatLogRepository.readChatMessages(chatRoomId, targetSequence, joinSequence) } returns listOf(chatNormalLog)

        val result = chatLogService.getChatLogs(chatRoomId, targetSequence, joinSequence)

        assert(result.size == 1)
        result.forEachIndexed { index, chatLog ->
            assert(chatLog.chatRoomId == chatRoomId)
            assert(chatLog.messageId == chatNormalLog.messageId)
            assert(chatLog.senderId == chatNormalLog.senderId)
        }
    }

    @Test
    fun `채팅 로그 가져오기`() {
        val messageId = "messageId"
        val chatRoomId = TestDataFactory.createChatRoomId()
        val userId = TestDataFactory.createUserId()
        val chatNormalLog = TestDataFactory.createChatNormalLog(
            messageId,
            chatRoomId,
            userId,
        )

        every { chatLogRepository.readChatMessage(messageId) } returns chatNormalLog

        val result = chatLogService.getChatLog(messageId)

        assert(result.chatRoomId == chatRoomId)
        assert(result.messageId == messageId)
        assert(result.senderId == userId)
    }

    @Test
    fun `키워드로 채팅 로그 가져오기`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val keyword = "keyword"
        val chatNormalLog = TestDataFactory.createChatNormalLog(
            "messageId",
            chatRoomId,
            TestDataFactory.createUserId(),
        )

        every { chatLogRepository.readChatKeyWordMessages(chatRoomId, keyword) } returns listOf(chatNormalLog)

        val result = chatLogService.getChatKeyWordMessages(chatRoomId, keyword)

        assert(result.size == 1)
        result.forEachIndexed { index, chatLog ->
            assert(chatLog.chatRoomId == chatRoomId)
            assert(chatLog.messageId == chatNormalLog.messageId)
            assert(chatLog.senderId == chatNormalLog.senderId)
        }
    }
}
