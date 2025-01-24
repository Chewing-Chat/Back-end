package org.chewing.v1.model.emoticon

class EmoticonInfo private constructor(
    val id: String,
    val name: String,
    val url: String,
    val emoticonPackId: String,
) {
    companion object {
        fun of(
            id: String,
            name: String,
            url: String,
            emoticonPackId: String,
        ): EmoticonInfo {
            return EmoticonInfo(
                id = id,
                name = name,
                url = url,
                emoticonPackId = emoticonPackId,
            )
        }
    }
}
