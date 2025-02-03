package org.chewing.v1.model.announcement

@JvmInline
value class AnnouncementId private constructor(
    val id: String,
) {
    companion object {
        fun of(
            id: String,
        ): AnnouncementId {
            return AnnouncementId(id)
        }
    }
}
