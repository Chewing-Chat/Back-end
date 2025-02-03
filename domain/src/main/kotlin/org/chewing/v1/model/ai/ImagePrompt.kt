package org.chewing.v1.model.ai

class ImagePrompt private constructor(
    val imageUrl: String,
) : Prompt() {
    companion object {
        fun of(text: String): ImagePrompt = ImagePrompt(text)
    }

    override val type = PromptType.IMAGE
}
