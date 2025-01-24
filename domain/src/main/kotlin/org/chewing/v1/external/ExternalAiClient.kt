package org.chewing.v1.external

import org.chewing.v1.model.ai.Prompt

interface ExternalAiClient {
    suspend fun prompt(prompts: List<Prompt>): String?
}
