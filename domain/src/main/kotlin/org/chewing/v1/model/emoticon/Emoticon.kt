package org.chewing.v1.model.emoticon

import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType

class Emoticon private constructor(
    val id: String,
    val name: String,
    val media: Media,
) {
    companion object {
        fun of(
            id: String,
            name: String,
            url: String,
        ): Emoticon {
            return Emoticon(
                id = id,
                name = name,
                media = Media.of(FileCategory.EMOTICON, url, 0, MediaType.IMAGE_PNG),
            )
        }
    }
}
