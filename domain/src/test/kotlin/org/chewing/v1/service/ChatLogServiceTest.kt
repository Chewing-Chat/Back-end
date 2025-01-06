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
import org.chewing.v1.implementation.chat.sequence.ChatFinder
import org.chewing.v1.implementation.chat.sequence.ChatSequenceReader
import org.chewing.v1.implementation.chat.sequence.ChatSequenceUpdater
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.model.chat.message.MessageType
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.repository.chat.ChatLogRepository
import org.chewing.v1.repository.chat.ChatSequenceRepository
import org.chewing.v1.service.chat.ChatLogService
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ChatLogServiceTest {
    private val fileHandler: FileHandler = mockk()
    private val chatLogRepository: ChatLogRepository = mockk()
    private val chatSequenceRepository: ChatSequenceRepository = mockk()

    private val chatSequenceReader: ChatSequenceReader = ChatSequenceReader(chatSequenceRepository)
    private val chatSequenceUpdater: ChatSequenceUpdater = ChatSequenceUpdater(chatSequenceRepository)
    private val chatFinder: ChatFinder = ChatFinder(chatSequenceReader, chatSequenceUpdater)
    private val chatAppender: ChatAppender = ChatAppender(chatLogRepository)
    private val chatReader: ChatReader = ChatReader(chatLogRepository)
    private val chatGenerator: ChatGenerator = ChatGenerator()
    private val chatRemover: ChatRemover = ChatRemover(chatLogRepository)

    private val chatLogService: ChatLogService = ChatLogService(
        fileHandler,
        chatFinder,
        chatAppender,
        chatReader,
        chatGenerator,
        chatRemover,
    )

    @Test
    fun `파일 업로드 메시지를 생성하고 저장해야함`() {
        val fileDataList = listOf(TestDataFactory.createFileData())
        val userId = "userId"
        val chatRoomId = "chatRoomId"
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatSequenceRepository.updateSequenceIncrement(chatRoomId) } returns seqNumber
        every {
            fileHandler.handleNewFiles(
                userId,
                fileDataList,
                FileCategory.CHAT,
            )
        } returns listOf(TestDataFactory.createMedia(FileCategory.CHAT, 0, MediaType.IMAGE_PNG))
        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.uploadFiles(fileDataList, userId, chatRoomId)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.number.chatRoomId == chatRoomId)
        assert(result.number.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.number.page == seqNumber.sequenceNumber / ChatFinder.PAGE_SIZE)
        assert(result.medias.size == 1)
        assert(result.type == MessageType.FILE)
        assert(result.medias[0].category == FileCategory.CHAT)
        assert(result.medias[0].type == MediaType.IMAGE_PNG)
        assert(result.medias[0].index == 0)
        assert(result.medias[0].url.isNotEmpty())
    }

    @Test
    fun `읽음 확인 메시지 생성`() {
        val chatRoomId = "chatRoomId"
        val userId = "userId"
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatSequenceRepository.readCurrent(chatRoomId) } returns seqNumber

        val result = chatLogService.readMessage(chatRoomId, userId)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.type == MessageType.READ)
        assert(result.number.chatRoomId == chatRoomId)
        assert(result.number.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.number.page == seqNumber.sequenceNumber / ChatFinder.PAGE_SIZE)
    }

    @Test
    fun `삭제 메시지 생성 및 기존 메시지 삭제`() {
        val chatRoomId = "chatRoomId"
        val userId = "userId"
        val messageId = "messageId"
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatSequenceRepository.updateSequenceIncrement(chatRoomId) } returns seqNumber
        every { chatLogRepository.removeLog(any()) } just Runs

        val result = chatLogService.deleteMessage(chatRoomId, userId, messageId)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.targetMessageId == messageId)
        assert(result.type == MessageType.DELETE)
        assert(result.number.chatRoomId == chatRoomId)
        assert(result.number.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.number.page == seqNumber.sequenceNumber / ChatFinder.PAGE_SIZE)
    }

    @Test
    fun `답장 메시지 생성 및 저장 - 일반 메시지`() {
        val chatRoomId = "chatRoomId"
        val userId = "userId"
        val parentMessageId = "parentMessageId"
        val text = "text"
        val time = LocalDateTime.now()
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)
        val parentChatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val parentChatLog =
            TestDataFactory.createChatNormalLog(parentMessageId, chatRoomId, userId, parentChatNumber, time)

        every { chatSequenceRepository.updateSequenceIncrement(chatRoomId) } returns seqNumber
        every { chatLogRepository.readChatMessage(parentMessageId) } returns parentChatLog
        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.replyMessage(chatRoomId, userId, parentMessageId, text)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.text == text)
        assert(result.type == MessageType.REPLY)
        assert(result.number.chatRoomId == chatRoomId)
        assert(result.number.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.number.page == seqNumber.sequenceNumber / ChatFinder.PAGE_SIZE)
        assert(result.parentMessageId == parentMessageId)
        assert(result.parentSeqNumber == parentChatNumber.sequenceNumber)
        assert(result.parentMessageType == parentChatLog.type)
        assert(result.parentMessageText == parentChatLog.text)
        assert(result.parentMessageId == parentChatLog.messageId)
        assert(result.parentMessagePage == parentChatNumber.page)
    }

    @Test
    fun `답장 메시지 생성 및 저장 - 파일 메시지`() {
        val chatRoomId = "chatRoomId"
        val userId = "userId"
        val parentMessageId = "parentMessageId"
        val text = "text"
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)
        val parentChatNumber = TestDataFactory.createChatNumber(chatRoomId)
        val parentChatLog = TestDataFactory.createChatFileLog(parentMessageId, chatRoomId, userId, parentChatNumber)

        every { chatSequenceRepository.updateSequenceIncrement(chatRoomId) } returns seqNumber
        every { chatLogRepository.readChatMessage(parentMessageId) } returns parentChatLog
        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.replyMessage(chatRoomId, userId, parentMessageId, text)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.text == text)
        assert(result.type == MessageType.REPLY)
        assert(result.number.chatRoomId == chatRoomId)
        assert(result.number.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.number.page == seqNumber.sequenceNumber / ChatFinder.PAGE_SIZE)
        assert(result.parentMessageId == parentMessageId)
        assert(result.parentSeqNumber == parentChatNumber.sequenceNumber)
        assert(result.parentMessageType == parentChatLog.type)
        assert(result.parentMessageText == parentChatLog.medias[0].url)
        assert(result.parentMessageId == parentChatLog.messageId)
        assert(result.parentMessagePage == parentChatNumber.page)
    }

    @Test
    fun `일반 메시지 생성 및 저장`() {
        val chatRoomId = "chatRoomId"
        val userId = "userId"
        val text = "text"
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatSequenceRepository.updateSequenceIncrement(chatRoomId) } returns seqNumber
        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.chatNormalMessage(chatRoomId, userId, text)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.text == text)
        assert(result.type == MessageType.NORMAL)
        assert(result.number.chatRoomId == chatRoomId)
        assert(result.number.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.number.page == seqNumber.sequenceNumber / ChatFinder.PAGE_SIZE)
    }

    @Test
    fun `채팅방 나가기 메시지 생성 및 저장`() {
        val chatRoomIds = listOf("chatRoomId")
        val userId = "userId"
        val seqNumber = listOf(TestDataFactory.createChatSequenceNumber(chatRoomIds[0]))

        every { chatSequenceRepository.updateSequenceIncrements(chatRoomIds) } returns seqNumber
        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.leaveMessages(chatRoomIds, userId)

        assert(result.size == 1)
        assert(result[0].chatRoomId == chatRoomIds[0])
        assert(result[0].senderId == userId)
        assert(result[0].type == MessageType.LEAVE)
        assert(result[0].number.chatRoomId == chatRoomIds[0])
        assert(result[0].number.sequenceNumber == seqNumber[0].sequenceNumber)
        assert(result[0].number.page == seqNumber[0].sequenceNumber / ChatFinder.PAGE_SIZE)
    }

    @Test
    fun `초대 메시지 리스트 생성 및 저장`() {
        val friendIds = listOf("friendId")
        val chatRoomId = "chatRoomId"
        val userId = "userId"
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatSequenceRepository.updateSequenceIncrement(chatRoomId) } returns seqNumber
        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.inviteMessages(friendIds, chatRoomId, userId)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.type == MessageType.INVITE)
        assert(result.number.chatRoomId == chatRoomId)
        assert(result.number.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.number.page == seqNumber.sequenceNumber / ChatFinder.PAGE_SIZE)
        assert(result.targetUserIds[0] == friendIds[0])
    }

    @Test
    fun `초대 메시지 생성 및 저장`() {
        val chatRoomId = "chatRoomId"
        val friendId = "friendId"
        val userId = "userId"
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatSequenceRepository.updateSequenceIncrement(chatRoomId) } returns seqNumber
        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.inviteMessage(chatRoomId, friendId, userId)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.type == MessageType.INVITE)
        assert(result.number.chatRoomId == chatRoomId)
        assert(result.number.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.number.page == seqNumber.sequenceNumber / ChatFinder.PAGE_SIZE)
        assert(result.targetUserIds[0] == friendId)
    }

    @Test
    fun `폭탄 메시지 생성 및 저장`() {
        val chatRoomId = "chatRoomId"
        val userId = "userId"
        val text = "text"
        val expiredAt = LocalDateTime.now()
        val seqNumber = TestDataFactory.createChatSequenceNumber(chatRoomId)

        every { chatSequenceRepository.updateSequenceIncrement(chatRoomId) } returns seqNumber
        every { chatLogRepository.appendChatLog(any()) } just Runs

        val result = chatLogService.bombingMessage(chatRoomId, userId, text, expiredAt)

        assert(result.chatRoomId == chatRoomId)
        assert(result.senderId == userId)
        assert(result.text == text)
        assert(result.type == MessageType.BOMB)
        assert(result.number.chatRoomId == chatRoomId)
        assert(result.number.sequenceNumber == seqNumber.sequenceNumber)
        assert(result.number.page == seqNumber.sequenceNumber / ChatFinder.PAGE_SIZE)
        assert(result.expiredAt == expiredAt)
    }

    @Test
    fun `최신 채팅 로그 가져오기`() {
        val chatRoomIds = listOf("chatRoomId")
        val seqNumbers = listOf(TestDataFactory.createChatSequenceNumber(chatRoomIds[0]))
        val chatNumbers = listOf(TestDataFactory.createChatNumber(chatRoomIds[0]))
        val time = LocalDateTime.now()
        val chatNormalLog =
            TestDataFactory.createChatNormalLog("messageId", chatRoomIds[0], "userId", chatNumbers[0], time)

        every { chatSequenceRepository.readCurrentSeqNumbers(chatRoomIds) } returns seqNumbers
        every { chatLogRepository.readLatestMessages(any()) } returns listOf(chatNormalLog)

        val result = chatLogService.getLatestChat(chatRoomIds)

        assert(result.size == 1)
        assert(result[0].chatRoomId == chatRoomIds[0])
        assert(result[0].number.chatRoomId == chatRoomIds[0])
        assert(result[0].number.sequenceNumber == chatNumbers[0].sequenceNumber)
        assert(result[0].number.page == chatNumbers[0].page)
        assert(result[0].messageId == chatNormalLog.messageId)
        assert(result[0].senderId == chatNormalLog.senderId)
    }

    @Test
    fun `채팅 로그 가져오기`() {
        val chatRoomId = "chatRoomId"
        val page = 0
        val time = LocalDateTime.now()
        val chatNormalLog = TestDataFactory.createChatNormalLog(
            "messageId",
            chatRoomId,
            "userId",
            TestDataFactory.createChatNumber(chatRoomId),
            time,
        )

        every { chatLogRepository.readChatMessages(chatRoomId, page) } returns listOf(chatNormalLog)

        val result = chatLogService.getChatLog(chatRoomId, page)

        assert(result.size == 1)
        assert(result[0].chatRoomId == chatRoomId)
        assert(result[0].messageId == chatNormalLog.messageId)
        assert(result[0].senderId == chatNormalLog.senderId)
    }
}
