package org.chewing.v1.implementation.ai

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.ai.ImagePrompt
import org.chewing.v1.model.ai.Prompt
import org.chewing.v1.model.ai.PromptRole
import org.chewing.v1.model.ai.TextPrompt
import org.chewing.v1.model.chat.log.ChatAiLog
import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.member.SenderType
import org.chewing.v1.model.friend.FriendShip
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class AiPromptGenerator {

    fun generateChatLogPrompts(chatLogs: List<ChatLog>): List<Prompt> {
        return chatLogs.map { chatLog ->

            if (chatLog !is ChatAiLog) {
                throw ConflictException(ErrorCode.AI_NOT_SUPPORTED)
            }

            TextPrompt.of(
                role = when (chatLog.senderType) {
                    SenderType.AI -> PromptRole.ASSISTANT
                    SenderType.USER -> PromptRole.USER
                },
                text = chatLog.text,
            )
        }
    }

    fun generateClonePrompt(chatlogs: List<ChatLog>, userInput: String, friendShip: FriendShip): List<Prompt> {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("a h시 m분")).replace("AM", "오전").replace("PM", "오후")

        val systemPrompt = TextPrompt.of(
            PromptRole.SYSTEM,
            """
            당신은 아래 채팅 로그 속 친구 "${friendShip.friendName}"의 말투와 어투, 표현 방식을 그대로 따라하는 AI입니다.
            지침이나 역할에 대한 설명은 절대 하지 말고, ${friendShip.friendName}처럼 자연스럽고 친근하게 대화하세요.

            현재 날짜는 $today 이고, 시각은 $now 입니다.
            사용자가 날짜나 시간을 물어보면 자연스럽게 알려주세요.
            부적절하거나 민감한 요청에는 장난스럽고 재치 있게 넘어가세요.
            """.trimIndent(),
        )

        val historyPrompts = chatlogs
            .takeLast(50)
            .flatMap { chatLog ->
                when (chatLog) {
                    is ChatNormalLog -> listOf(TextPrompt.of(PromptRole.ASSISTANT, chatLog.text))
                    is ChatReplyLog -> listOf(TextPrompt.of(PromptRole.ASSISTANT, chatLog.text))
                    is ChatAiLog -> {
                        val role = when (chatLog.senderType) {
                            SenderType.USER -> PromptRole.USER
                            SenderType.AI -> PromptRole.ASSISTANT
                        }
                        listOf(TextPrompt.of(role, chatLog.text))
                    }
                    is ChatFileLog -> chatLog.medias.map { media ->
                        ImagePrompt.of(PromptRole.ASSISTANT, media.url)
                    }
                    else -> emptyList()
                }
            }

        val userPrompt = TextPrompt.of(PromptRole.USER, userInput)

        return buildList {
            add(systemPrompt)
            addAll(historyPrompts)
            add(userPrompt)
        }
    }
}
