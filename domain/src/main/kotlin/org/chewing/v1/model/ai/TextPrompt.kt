package org.chewing.v1.model.ai

class TextPrompt private constructor(
    val text: String,
) : Prompt() {
    companion object {
        fun of(text: String): TextPrompt = TextPrompt(text)
    }
    override val type = PromptType.TEXT
}
