package org.chewing.v1.implementation.ai

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.ai.Prompt
import org.chewing.v1.model.ai.PromptRole
import org.chewing.v1.model.ai.TextPrompt
import org.chewing.v1.model.chat.log.ChatAiLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.member.SenderType
import org.springframework.stereotype.Component

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

    fun generateClonePrompt(chatlogs: List<ChatLog>, prompt: String): List<Prompt> {
        val messagePrompts = chatlogs
            .filter { it is ChatNormalLog || it is ChatReplyLog || it is ChatAiLog }
            .takeLast(20)
            .mapNotNull {
                val text = when (it) {
                    is ChatNormalLog -> it.text
                    is ChatReplyLog -> it.text
                    else -> null
                }
                text?.let {
                    TextPrompt.of(
                        role = PromptRole.USER,
                        text = it,
                    )
                }
            }

        val finalPrompt = TextPrompt.of(
            role = PromptRole.USER,
            text = "채팅 로그를 분석해서 대화 문맥에 따라 다음 대화에 채팅로그의 사용자가 너라고 생각하고 사용자의 말투와 똑같이 답해줘, 예를 들어 사용자가 반말중이면 반말하고," +
                "공룡이 주제인거 같으면 공룡에 대해서 답변하면 된다. :\n\n$prompt",
        )

        return messagePrompts + finalPrompt
    }
}
