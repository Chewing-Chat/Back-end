package org.chewing.v1.implementation.ai

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.external.ExternalAiClient
import org.chewing.v1.model.ai.Prompt
import org.chewing.v1.util.AsyncJobExecutor
import org.springframework.stereotype.Component

@Component
class AiSender(
    private val externalAiClient: ExternalAiClient,
    private val asyncJobExecutor: AsyncJobExecutor,
) {
    fun sendPrompt(prompts: List<Prompt>): String {
        return asyncJobExecutor.executeAsyncReturnJob(prompts) {
            externalAiClient.prompt(prompts) ?: throw ConflictException(ErrorCode.AI_PROMPT_FAILED)
        }
    }
}
