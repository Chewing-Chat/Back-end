package org.chewing.v1.service.ai

import org.chewing.v1.implementation.ai.AiSender
import org.chewing.v1.model.ai.Prompt
import org.chewing.v1.model.chat.log.ChatLog
import org.springframework.stereotype.Service

@Service
class AiPromptService (
    private val aiSender: AiSender
){
    fun prompt(chatlogs: List<ChatLog>): String {
        return aiSender.sendPrompt(prompts)
    }
}
