package org.chewing.v1.controller

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.emoticon.EmoticonController
import org.chewing.v1.service.emoticon.EmoticonService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
class EmoticonControllerTest : RestDocsTest() {
    private lateinit var emoticonService: EmoticonService
    private lateinit var emoticonController: EmoticonController

    @BeforeEach
    fun setUp() {
        emoticonService = mockk()
        emoticonController = EmoticonController(emoticonService)
        mockMvc = mockController(emoticonController)
    }

    @Test
    fun `소유 하고 있는 이모티콘 팩 목록 가져오기`() {
        val userId = "userId"
        val emoticonId = "emoticonId"
        val emoticonPackId = "emoticonPackId"
        val emoticon = TestDataFactory.createEmoticon(emoticonId)
        val emoticonPack = TestDataFactory.createEmoticonPack(emoticonPackId, listOf(emoticon))

        every { emoticonService.fetchUserEmoticonPacks(userId) } returns listOf(emoticonPack)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/emoticon/list")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId),

        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.emoticonPacks[0].emoticonPackId").value(emoticonPackId))
            .andExpect(jsonPath("$.data.emoticonPacks[0].fileUrl").value(emoticonPack.media.url))
            .andExpect(jsonPath("$.data.emoticonPacks[0].fileType").value("image/png"))
            .andExpect(jsonPath("$.data.emoticonPacks[0].emoticons[0].emoticonId").value(emoticonId))
            .andExpect(jsonPath("$.data.emoticonPacks[0].emoticons[0].name").value(emoticon.name))
            .andExpect(jsonPath("$.data.emoticonPacks[0].emoticons[0].fileUrl").value(emoticon.media.url))
            .andExpect(jsonPath("$.data.emoticonPacks[0].emoticons[0].fileType").value("image/png"))
            .andReturn()
    }
}
