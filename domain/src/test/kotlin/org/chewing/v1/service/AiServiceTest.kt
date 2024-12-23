package org.chewing.v1.service

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.external.ExternalAiClient
import org.chewing.v1.implementation.ai.AiSender
import org.chewing.v1.implementation.ai.PromptGenerator
import org.chewing.v1.model.ai.ImagePrompt
import org.chewing.v1.model.ai.Prompt
import org.chewing.v1.model.ai.TextPrompt
import org.chewing.v1.service.ai.AiService
import org.chewing.v1.util.AsyncJobExecutor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.format.DateTimeFormatter
import java.util.Locale

class AiServiceTest {
    private val externalAiClient: ExternalAiClient = mockk()
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val jobExecutor = AsyncJobExecutor(ioScope)
    private val aiSender: AiSender = AiSender(jobExecutor, externalAiClient)
    private val promptGenerator: PromptGenerator = PromptGenerator()

    private val aiService = AiService(promptGenerator, aiSender)
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M월 d일 a h시", Locale.KOREAN)

    @Test
    fun `AI 최근 요약 정보 생성 테스트`() {
        val friendName = TestDataFactory.createUserName()
        val feedId = "feedId"
        val userId = "userId"
        val feeds = listOf(TestDataFactory.createFeed(feedId, userId))
        val promptsSlot = slot<List<Prompt>>()

        coEvery { externalAiClient.prompt(capture(promptsSlot)) } returns "promptResult"

        val result = aiService.getAiRecentSummary(friendName, feeds)

        assert("promptResult" == result, { "AI 서비스가 예상한 결과를 반환해야 합니다." })
        assert(5 == promptsSlot.captured.size, { "프롬프트의 개수는 5개여야 합니다." })

        promptsSlot.captured.forEachIndexed { index, prompt ->
            when (index) {
                0 -> {
                    assert(prompt is TextPrompt, { "첫 번째 프롬프트는 TextPrompt 타입이어야 합니다." })
                    prompt as TextPrompt
                    val expectedName = "${friendName.firstName}${friendName.lastName}"
                    assert(
                        prompt.text.contains(expectedName),
                        { "첫 번째 프롬프트는 친구 이름 '$expectedName'을 포함해야 합니다." },
                    )
                }
                1 -> {
                    assert(prompt is TextPrompt, { "두 번째 프롬프트는 TextPrompt 타입이어야 합니다." })
                    prompt as TextPrompt
                    assert(
                        prompt.text.contains(feeds[0].feed.topic),
                        { "두 번째 프롬프트는 피드의 주제 '${feeds[0].feed.topic}'을 포함해야 합니다." },
                    )
                }
                2 -> {
                    assert(prompt is TextPrompt, { "세 번째 프롬프트는 TextPrompt 타입이어야 합니다." })
                    prompt as TextPrompt
                    val formattedDate = dateFormatter.format(feeds[0].feed.uploadAt)
                    assert(
                        prompt.text.contains(formattedDate),
                        { "세 번째 프롬프트는 업로드 날짜 '$formattedDate'를 포함해야 합니다." },
                    )
                }
                3 -> {
                    assert(prompt is ImagePrompt, { "네 번째 프롬프트는 ImagePrompt 타입이어야 합니다." })
                    prompt as ImagePrompt
                    assert(
                        prompt.imageUrl.contains(feeds[0].feedDetails[0].media.url),
                        { "네 번째 프롬프트는 이미지 URL '${feeds[0].feedDetails[0].media.url}'을 포함해야 합니다." },
                    )
                }
            }
        }

        coVerify(exactly = 1) { externalAiClient.prompt(any()) }
        confirmVerified(externalAiClient)
    }

    @Test
    fun `AI 최근 요약 정보 생성 테스트 - 실패`() {
        val friendName = TestDataFactory.createUserName()
        val feedId = "feedId"
        val userId = "userId"
        val feeds = listOf(TestDataFactory.createFeed(feedId, userId))

        coEvery { externalAiClient.prompt(any()) } returns null

        val exception =
            assertThrows<ConflictException>(
                "AI 서비스가 예상한 결과를 반환해야 합니다.",
            ) { aiService.getAiRecentSummary(friendName, feeds) }

        assert(exception.errorCode == ErrorCode.AI_CREATE_FAILED, { "AI 서비스 오류 코드가 일치해야 합니다." })
    }

    @Test
    fun `AI 검색 채팅 생성 테스트`() {
        val promptText = "prompt"
        val promptsSlot = slot<List<Prompt>>()

        coEvery { externalAiClient.prompt(capture(promptsSlot)) } returns "promptResult"

        val result = aiService.getAiSearchChat(promptText)

        assert("promptResult" == result, { "AI 서비스가 예상한 결과를 반환해야 합니다." })
        assert(2 == promptsSlot.captured.size, { "프롬프트의 개수는 2개여야 합니다." })

        promptsSlot.captured.forEachIndexed { index, prompt ->
            when (index) {
                0 -> {
                    assert(prompt is TextPrompt, { "첫 번째 프롬프트는 TextPrompt 타입이어야 합니다." })
                    prompt as TextPrompt
                }
                1 -> {
                    assert(prompt is TextPrompt, { "두 번째 프롬프트는 TextPrompt 타입이어야 합니다." })
                    prompt as TextPrompt
                    assert(
                        prompt.text.contains(promptText),
                        { "두 번째 프롬프트는 검색어 '$prompt'을 포함해야 합니다." },
                    )
                }
            }
        }

        coVerify(exactly = 1) { externalAiClient.prompt(any()) }
        confirmVerified(externalAiClient)
    }

    @Test
    fun `AI 검색 채팅 생성 테스트 - 실패`() {
        val promptText = "prompt"

        coEvery { externalAiClient.prompt(any()) } returns null

        val exception =
            assertThrows<ConflictException>(
                "AI 서비스가 예상한 결과를 반환해야 합니다.",
            ) { aiService.getAiSearchChat(promptText) }

        assert(exception.errorCode == ErrorCode.AI_CREATE_FAILED, { "AI 서비스 오류 코드가 일치해야 합니다." })
    }

    @Test
    fun `AI 일정 생성 테스트`() {
        val promptText = "prompt"
        val promptsSlot = slot<List<Prompt>>()

        coEvery { externalAiClient.prompt(capture(promptsSlot)) } returns "promptResult"

        val result = aiService.getAiSchedule(promptText)

        assert("promptResult" == result, { "AI 서비스가 예상한 결과를 반환해야 합니다." })
        assert(2 == promptsSlot.captured.size, { "프롬프트의 개수는 2개여야 합니다." })

        promptsSlot.captured.forEachIndexed { index, prompt ->
            when (index) {
                0 -> {
                    assert(prompt is TextPrompt, { "첫 번째 프롬프트는 TextPrompt 타입이어야 합니다." })
                    prompt as TextPrompt
                }
                1 -> {
                    assert(prompt is TextPrompt, { "두 번째 프롬프트는 TextPrompt 타입이어야 합니다." })
                    prompt as TextPrompt
                    assert(
                        prompt.text.contains(promptText),
                        { "두 번째 프롬프트는 검색어 '$prompt'을 포함해야 합니다." },
                    )
                }
            }
        }

        coVerify(exactly = 1) { externalAiClient.prompt(any()) }
        confirmVerified(externalAiClient)
    }
}
