package org.chewing.v1.dto.response.media

import org.chewing.v1.model.media.Media

data class MediaResponse(
    val fileType: String,
    val fileUrl: String,
    val index: Int,
) {
    companion object {
        fun from(media: Media): MediaResponse {
            return MediaResponse(
                fileType = media.type.value().lowercase(),
                fileUrl = media.url,
                index = media.index,
            )
        }
    }
}
