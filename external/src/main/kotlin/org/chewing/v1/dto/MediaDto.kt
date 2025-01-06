package org.chewing.v1.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.chewing.v1.model.media.Media

data class MediaDto @JsonCreator constructor(
    @JsonProperty("fileType") val fileType: String,
    @JsonProperty("fileUrl") val fileUrl: String,
    @JsonProperty("index") val index: Int,
) {
    companion object {
        fun from(media: Media): MediaDto = MediaDto(
            fileType = media.type.value().lowercase(),
            fileUrl = media.url,
            index = media.index,
        )
    }
}
