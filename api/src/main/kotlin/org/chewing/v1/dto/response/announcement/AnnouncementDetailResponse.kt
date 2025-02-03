package org.chewing.v1.dto.response.announcement

import org.chewing.v1.model.announcement.Announcement

data class AnnouncementDetailResponse(
    val content: String,
) {
    companion object {
        fun of(
            announcement: Announcement,
        ): AnnouncementDetailResponse {
            return AnnouncementDetailResponse(
                content = announcement.content,
            )
        }
    }
}
