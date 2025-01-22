package org.chewing.v1.model.feed

@JvmInline
value class FeedDetailId private constructor(
    val id: String,
) {
    companion object {
        fun of(
            id: String,
        ): FeedDetailId {
            return FeedDetailId(id)
        }
    }
}
