package org.chewing.v1.repository.support

import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.springframework.stereotype.Component

@Component
object MediaProvider {
    fun buildProfileContent(): Media {
        return Media.of(FileCategory.PROFILE, "www.example.com", 0, MediaType.IMAGE_PNG)
    }

    fun buildFeedContent(index: Int): Media {
        return Media.of(FileCategory.FEED, "www.example.com", index, MediaType.IMAGE_PNG)
    }

    fun buildFeedContents(): List<Media> {
        return listOf(buildFeedContent(1), buildFeedContent(0))
    }
    fun buildChatContent(): Media {
        return Media.of(FileCategory.CHAT, "www.example.com", 0, MediaType.IMAGE_PNG)
    }
}
