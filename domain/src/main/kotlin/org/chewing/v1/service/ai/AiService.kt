package org.chewing.v1.service.ai

import org.chewing.v1.implementation.ai.AiSender
import org.chewing.v1.model.ai.Prompt
import org.springframework.stereotype.Service

@Service
class AiService (
    private val aiSender: AiSender
){
    fun prompt(prompts: List<Prompt>): String {
        return aiSender.sendPrompt(prompts)
    }
}
