package org.chewing.v1.model.ai

sealed class Prompt {
    abstract val role: PromptRole
    abstract val type: PromptType
}
