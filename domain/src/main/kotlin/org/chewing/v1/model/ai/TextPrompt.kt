package org.chewing.v1.model.ai

class TextPrompt private constructor(
    override val role: PromptRole,
    val text: String,
) : Prompt() {
    companion object {
        fun of(role: PromptRole, text: String): TextPrompt = TextPrompt(role, text)
    }
    override val type = PromptType.TEXT
}
