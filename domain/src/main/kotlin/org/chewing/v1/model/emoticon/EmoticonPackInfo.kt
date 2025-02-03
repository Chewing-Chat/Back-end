package org.chewing.v1.model.emoticon

class EmoticonPackInfo private constructor(
    val id: String,
    val name: String,
    val url: String,
) {
    companion object {
        fun of(
            emoticonPackId: String,
            name: String,
            url: String,
        ): EmoticonPackInfo {
            return EmoticonPackInfo(
                id = emoticonPackId,
                name = name,
                url = url,
            )
        }
    }
}
