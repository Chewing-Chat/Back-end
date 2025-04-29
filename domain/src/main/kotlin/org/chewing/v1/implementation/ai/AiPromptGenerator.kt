package org.chewing.v1.implementation.ai

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.ai.Prompt
import org.chewing.v1.model.ai.PromptRole
import org.chewing.v1.model.ai.TextPrompt
import org.chewing.v1.model.chat.log.ChatAiLog
import org.chewing.v1.model.chat.log.ChatLog
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
}
