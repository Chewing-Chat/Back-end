package org.chewing.v1.controller

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.ai.AiController
import org.chewing.v1.dto.request.ai.AiRequest
import org.chewing.v1.facade.AiFacade
import org.chewing.v1.model.ai.DateTarget
import org.chewing.v1.util.converter.StringToDateTargetConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
class AiControllerTest : RestDocsTest() {
    private lateinit var aiFacade: AiFacade
    private lateinit var aiController: AiController

    @BeforeEach
    fun setUp() {
        aiFacade = mockk()
        aiController = AiController(aiFacade)
        mockMvc = mockController(aiController)

        mockMvc = mockControllerWithCustomConverter(
            aiController,
            StringToDateTargetConverter(),
        )
    }

    @Test
    fun `AI 챗봇 친구 근황 요약하기`() {
        val userId = "testUserId"
        val friendId = "testFriendId"
        val dateTarget = "weekly"
        val summary = "testSummary"

        every { aiFacade.getAiRecentSummary(userId, friendId, DateTarget.WEEKLY) } returns summary

        mockMvc.perform(
            get("/api/ai/friend/$friendId/summary")
                .param("targetDate", dateTarget)
                .requestAttr("userId", userId),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.summary").value(summary))
    }

    @Test
    fun `AI 챗본 채팅 검색`() {
        val chatRoomId = "testChatRoomId"
        val userId = "testUserId"
        val messageId = "testMessageId"
        val prompt = "testPrompt"

        val requestBody = AiRequest.ChatSearch(
            chatRoomId = chatRoomId,
            prompt = prompt,
        )

        val chatLog = TestDataFactory.createNormalLog(
            messageId = messageId,
            userId = userId,
            chatRoomId = chatRoomId,
        )

        every { aiFacade.getAiSearchChat(chatRoomId, prompt) } returns listOf(chatLog)

        mockMvc.perform(
            post("/api/ai/chat/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.chatLogs[0].messageId").value(chatLog.messageId))
    }

    @Test
    fun `AI 챗봇 스케줄 생성`() {
        val userId = "testUserId"
        val prompt = "testPrompt"
        val scheduleId = "testScheduleId"
        val requestBody = AiRequest.Schedule(
            prompt = prompt,
        )

        every { aiFacade.createAiSchedule(userId, prompt) } returns scheduleId

        mockMvc.perform(
            post("/api/ai/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", userId),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.scheduleId").value(scheduleId))
    }
}
