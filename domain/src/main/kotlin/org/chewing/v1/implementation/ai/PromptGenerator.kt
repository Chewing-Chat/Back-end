package org.chewing.v1.implementation.ai

import org.chewing.v1.model.ai.ImagePrompt
import org.chewing.v1.model.ai.Prompt
import org.chewing.v1.model.ai.TextPrompt
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.user.UserName
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter
import java.util.Locale

@Component
class PromptGenerator {

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M월 d일 a h시", Locale.KOREAN)

    fun generateRecentSummaryPrompt(friendName: UserName, feeds: List<Feed>): List<Prompt> = sequenceOf(
        TextPrompt.of(
            "다음은 ${friendName.firstName + friendName.lastName}님의 최근 피드 정보입니다. 텍스트와 이미지 설명이 포함되어 있습니다.",
        ),
    )
        .plus(
            feeds.flatMap { feed ->
                sequenceOf(
                    TextPrompt.of(feed.feed.topic),
                    TextPrompt.of("📅 ${feed.feed.uploadAt.format(dateFormatter) + "에 올린 사진 📸"}"),
                )
                    .plus(feed.feedDetails.map { detail -> ImagePrompt.of(detail.media.url) })
            },
        ).toList()
        .plus(
            listOf(
                TextPrompt.of(
                    "이 정보를 기반으로 ${friendName.firstName + friendName.lastName}님의 근황을 한글로 각각 주제당 3줄로 요약해주세요. " +
                        "마지막에는 친구와의 대화를 위해 필요한 주제도 함께 알려주세요. " +
                        "출력 형식은 아래 예시를 참고하여 다음과 같이 해주세요:\n\n" +
                        "```\n" +
                        "시간: 8월 1일 오전 7시\n" +
                        "근황: 아침 햇살을 맞으며 하루를 시작하고 있습니다. 상쾌한 아침 기운으로 기분이 좋습니다. 새로운 하루를 설레는 마음으로 맞이하고 있습니다.\n\n" +
                        "시간: 8월 3일 오후 12시\n" +
                        "근황: 회사 근처 식당에서 동료들과 함께 점심 식사를 즐기고 있습니다. 여유로운 시간을 보내며 동료들과의 유대감을 강화하고 있습니다. 맛있는 음식을 함께 나누며 행복한 시간을 보내고 있습니다.\n" +
                        "```\n\n" +
                        "**대화를 위한 주제 추천:**\n" +
                        "1. 아침 루틴과 하루 계획에 대해 이야기해보세요.\n" +
                        "2. 동료들과의 점심 시간 경험을 공유해보세요.\n" +
                        "3. 두바이 초콜릿에 대한 관심과 맛있는 초콜릿 추천을 나눠보세요.\n",
                ),
            ),
        )

    fun generateSearchChatPrompt(prompt: String): List<Prompt> = sequenceOf(
        TextPrompt.of(
            "아래는 특정 채팅 검색 질의를 기반으로 몽고디비에서 효과적으로 검색할 수 있도록 설계된 키워드입니다. 검색 시스템이 지원할 수 있는 명사 단어만을 포함하고," +
                " 관련된 유사한 의미를 가진 단어도 함께 문맥을 고려하여 검색 범위를 확장해주세요. 키워드는 검색 가능성을 높이기 위해 짧고 간결하게 제공하며," +
                " 최대 10개의 단어를 쉼표로 구분된 목록 형태로 출력해주세요. 예시: 단어1, 단어2, 단어3",
        ),
        TextPrompt.of("검색어: $prompt"),
    ).toList()

    fun generateSchedulePrompt(prompt: String): List<Prompt> = sequenceOf(
        TextPrompt.of(
            "다음 문장을 분석하여 이떄 상상력을 더해 스케줄 정보를 아래 형식에 맞춰 작성해주세요.\n\n" +
                "예시 형식:\ntitle: 밥먹기\nmemo: 꼭 먹어야돼\nlocation: 집에서\n" +
                "startAt: 2024-04-27T12:00:00\nendAt: 2024-04-27T13:00:00\n" +
                "notificationAt: 2024-04-27T13:00:00\n\n",
        ),
        TextPrompt.of("일정: $prompt"),
    ).toList()
}
