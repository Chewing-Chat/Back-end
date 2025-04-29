package org.chewing.v1.model.ai

class ImagePrompt private constructor(
    override val role: PromptRole,
    val imageUrl: String,
) : Prompt() {
    companion object {
        fun of(role: PromptRole, text: String): ImagePrompt = ImagePrompt(role, text)
    }

    override val type = PromptType.IMAGE
}
