package org.chewing.v1.service.ai

import org.chewing.v1.implementation.ai.AiPromptGenerator
import org.chewing.v1.implementation.ai.AiSender
import org.chewing.v1.model.chat.log.ChatLog
import org.springframework.stereotype.Service

@Service
class AiPromptService(
    private val aiSender: AiSender,
    private val aiPromptGenerator: AiPromptGenerator,
) {
    fun prompt(chatlogs: List<ChatLog>): String {
        val prompts = aiPromptGenerator.generateChatLogPrompts(chatlogs)
        return aiSender.sendPrompt(prompts)
    }
}
