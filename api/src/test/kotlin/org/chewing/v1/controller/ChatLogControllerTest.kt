package org.chewing.v1.controller

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.chat.ChatLogController
import org.chewing.v1.service.chat.ChatLogService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class ChatLogControllerTest : RestDocsTest() {
    private lateinit var chatLogService: ChatLogService
    private lateinit var chatLogController: ChatLogController

    @BeforeEach
    fun setUp() {
        chatLogService = mockk()
        chatLogController = ChatLogController(chatLogService)
        mockMvc = mockController(chatLogController)
    }

    @Test
    fun `채팅 로그 가져오기`() {
        val userId = "testUserId"
        val chatRoomId = "testChatRoomId"
        val page = 0
        val messageId1 = "testMessageId1"
        val messageId2 = "testMessageId2"
        val messageId3 = "testMessageId3"
        val messageId4 = "testMessageId4"
        val messageId5 = "testMessageId5"
        val messageId6 = "testMessageId6"
        val chatFileLog = TestDataFactory.createFileLog(messageId1, chatRoomId, userId)
        val chatReplyLog = TestDataFactory.createReplyLog(messageId2, chatRoomId, userId)
        val chatLeaveLog = TestDataFactory.createLeaveLog(messageId3, chatRoomId, userId)
        val chatInviteLog = TestDataFactory.createInviteLog(messageId4, chatRoomId, userId)
        val chatBombLog = TestDataFactory.createBombLog(messageId5, chatRoomId, userId)
        val chatNormalLog = TestDataFactory.createNormalLog(messageId6, chatRoomId, userId)

        every { chatLogService.getChatLog(chatRoomId, page) } returns listOf(
            chatFileLog,
            chatReplyLog,
            chatLeaveLog,
            chatInviteLog,
            chatBombLog,
            chatNormalLog,
        )
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/chatRooms/$chatRoomId/log")
                .param("page", "0"),
        )
        result
            .andExpect(MockMvcResultMatchers.status().isOk)
            // 파일 메시지
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.chatLogs[0].messageId").value(messageId1))
            .andExpect(jsonPath("$.data.chatLogs[0].type").value(chatFileLog.type.name.lowercase()))
            .andExpect(jsonPath("$.data.chatLogs[0].chatRoomId").value(chatRoomId))
            .andExpect(jsonPath("$.data.chatLogs[0].senderId").value(userId))
            .andExpect(jsonPath("$.data.chatLogs[0].seqNumber").value(chatFileLog.number.sequenceNumber))
            .andExpect(jsonPath("$.data.chatLogs[0].page").value(chatFileLog.number.page))
            .andExpect(jsonPath("$.data.chatLogs[0].files[0].fileType").value(chatFileLog.medias[0].type.value().lowercase()))
            .andExpect(jsonPath("$.data.chatLogs[0].files[0].fileUrl").value(chatFileLog.medias[0].url))
            .andExpect(jsonPath("$.data.chatLogs[0].files[0].index").value(chatFileLog.medias[0].index))
            .andExpect(jsonPath("$.data.chatLogs[0].timestamp").value(chatFileLog.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))))
            // 답장 메시지
            .andExpect(jsonPath("$.data.chatLogs[1].messageId").value(messageId2))
            .andExpect(jsonPath("$.data.chatLogs[1].type").value(chatReplyLog.type.name.lowercase()))
            .andExpect(jsonPath("$.data.chatLogs[1].chatRoomId").value(chatRoomId))
            .andExpect(jsonPath("$.data.chatLogs[1].senderId").value(userId))
            .andExpect(jsonPath("$.data.chatLogs[1].seqNumber").value(chatReplyLog.number.sequenceNumber))
            .andExpect(jsonPath("$.data.chatLogs[1].page").value(chatReplyLog.number.page))
            .andExpect(jsonPath("$.data.chatLogs[1].parentMessageId").value(chatReplyLog.parentMessageId))
            .andExpect(jsonPath("$.data.chatLogs[1].parentMessagePage").value(chatReplyLog.parentMessagePage))
            .andExpect(jsonPath("$.data.chatLogs[1].parentSeqNumber").value(chatReplyLog.parentSeqNumber))
            .andExpect(jsonPath("$.data.chatLogs[1].parentMessageText").value(chatReplyLog.parentMessageText))
            .andExpect(jsonPath("$.data.chatLogs[1].parentMessageType").value(chatReplyLog.parentMessageType.name.lowercase()))
            .andExpect(jsonPath("$.data.chatLogs[1].text").value(chatReplyLog.text))
            .andExpect(jsonPath("$.data.chatLogs[1].timestamp").value(chatReplyLog.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))))
            // 채팅 나가기
            .andExpect(jsonPath("$.data.chatLogs[2].messageId").value(messageId3))
            .andExpect(jsonPath("$.data.chatLogs[2].type").value(chatLeaveLog.type.name.lowercase()))
            .andExpect(jsonPath("$.data.chatLogs[2].chatRoomId").value(chatRoomId))
            .andExpect(jsonPath("$.data.chatLogs[2].senderId").value(userId))
            .andExpect(jsonPath("$.data.chatLogs[2].seqNumber").value(chatLeaveLog.number.sequenceNumber))
            .andExpect(jsonPath("$.data.chatLogs[2].page").value(chatLeaveLog.number.page))
            .andExpect(jsonPath("$.data.chatLogs[2].timestamp").value(chatLeaveLog.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))))
            // 초대 메시지
            .andExpect(jsonPath("$.data.chatLogs[3].messageId").value(messageId4))
            .andExpect(jsonPath("$.data.chatLogs[3].type").value(chatInviteLog.type.name.lowercase()))
            .andExpect(jsonPath("$.data.chatLogs[3].chatRoomId").value(chatRoomId))
            .andExpect(jsonPath("$.data.chatLogs[3].senderId").value(userId))
            .andExpect(jsonPath("$.data.chatLogs[3].seqNumber").value(chatInviteLog.number.sequenceNumber))
            .andExpect(jsonPath("$.data.chatLogs[3].page").value(chatInviteLog.number.page))
            .andExpect(jsonPath("$.data.chatLogs[3].targetUserIds[0]").value(chatInviteLog.targetUserIds[0]))
            .andExpect(jsonPath("$.data.chatLogs[3].timestamp").value(chatInviteLog.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))))
            // 폭탄 메시지
            .andExpect(jsonPath("$.data.chatLogs[4].messageId").value(messageId5))
            .andExpect(jsonPath("$.data.chatLogs[4].type").value(chatBombLog.type.name.lowercase()))
            .andExpect(jsonPath("$.data.chatLogs[4].chatRoomId").value(chatRoomId))
            .andExpect(jsonPath("$.data.chatLogs[4].senderId").value(userId))
            .andExpect(jsonPath("$.data.chatLogs[4].seqNumber").value(chatBombLog.number.sequenceNumber))
            .andExpect(jsonPath("$.data.chatLogs[4].page").value(chatBombLog.number.page))
            .andExpect(jsonPath("$.data.chatLogs[4].text").value(chatBombLog.text))
            .andExpect(jsonPath("$.data.chatLogs[4].timestamp").value(chatBombLog.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))))
            .andExpect(jsonPath("$.data.chatLogs[4].expiredAt").value(chatBombLog.expiredAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))))
            // 일반 메시지
            .andExpect(jsonPath("$.data.chatLogs[5].messageId").value(messageId6))
            .andExpect(jsonPath("$.data.chatLogs[5].type").value(chatNormalLog.type.name.lowercase()))
            .andExpect(jsonPath("$.data.chatLogs[5].chatRoomId").value(chatRoomId))
            .andExpect(jsonPath("$.data.chatLogs[5].senderId").value(userId))
            .andExpect(jsonPath("$.data.chatLogs[5].seqNumber").value(chatNormalLog.number.sequenceNumber))
            .andExpect(jsonPath("$.data.chatLogs[5].page").value(chatNormalLog.number.page))
            .andExpect(jsonPath("$.data.chatLogs[5].text").value(chatNormalLog.text))
            .andExpect(jsonPath("$.data.chatLogs[5].timestamp").value(chatNormalLog.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))))
    }
}
