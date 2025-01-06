package org.chewing.v1.model.emoticon

import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType

class EmoticonPack private constructor(
    val id: String,
    val name: String,
    val media: Media,
    val emoticons: List<Emoticon>,
) {
    companion object {
        fun of(
            id: String,
            name: String,
            url: String,
            emoticons: List<Emoticon>,
        ): EmoticonPack {
            return EmoticonPack(
                id = id,
                name = name,
                media = Media.of(FileCategory.EMOTICON, url, 0, MediaType.IMAGE_PNG),
                emoticons = emoticons,
            )
        }
    }
}
