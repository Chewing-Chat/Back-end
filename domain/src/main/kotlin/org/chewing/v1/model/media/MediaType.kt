package org.chewing.v1.model.media

enum class MediaType(val type: String) {
    IMAGE_BASIC("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_JPG("image/jpg"),
    IMAGE_PNG("image/png"),
    IMAGE_HEIC("image/heic"),
    IMAGE_WEBP("image/webp"),
    VIDEO_BASIC("video/mp4"),
    TEXT_HTML("text/html"),
    APPLICATION_PDF("application/pdf"),
    AUDIO_MP3("audio/mp3"),
    VIDEO_MP4("video/mp4"),
    ;

    // pdf 추가

    fun value(): String {
        return type
    }

    companion object {
        fun fromType(type: String): MediaType? {
            return entries.find { it.type.equals(type, ignoreCase = true) }
        }
    }
}
