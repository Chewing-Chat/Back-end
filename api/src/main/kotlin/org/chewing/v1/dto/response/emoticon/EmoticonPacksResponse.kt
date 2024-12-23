package org.chewing.v1.dto.response.emoticon

import org.chewing.v1.model.emoticon.Emoticon
import org.chewing.v1.model.emoticon.EmoticonPack

data class EmoticonPacksResponse(
    val emoticonPacks: List<EmoticonPackResponse>,
) {
    companion object {
        fun of(emoticonPacks: List<EmoticonPack>): EmoticonPacksResponse {
            return EmoticonPacksResponse(
                emoticonPacks = emoticonPacks.map { EmoticonPackResponse.of(it) },
            )
        }
    }

    data class EmoticonPackResponse(
        val emoticonPackId: String,
        val fileUrl: String,
        val fileType: String,
        val emoticons: List<EmoticonResponse>,
    ) {
        companion object {
            fun of(emoticonPack: EmoticonPack): EmoticonPackResponse {
                return EmoticonPackResponse(
                    emoticonPackId = emoticonPack.id,
                    fileUrl = emoticonPack.media.url,
                    fileType = emoticonPack.media.type.value(),
                    emoticons = emoticonPack.emoticons.map { EmoticonResponse.of(it) },
                )
            }
        }

        data class EmoticonResponse(
            val emoticonId: String,
            val name: String,
            val fileUrl: String,
            val fileType: String,
        ) {
            companion object {
                fun of(emoticon: Emoticon): EmoticonResponse {
                    return EmoticonResponse(
                        emoticonId = emoticon.id,
                        name = emoticon.name,
                        fileUrl = emoticon.media.url,
                        fileType = emoticon.media.type.value(),
                    )
                }
            }
        }
    }
}
