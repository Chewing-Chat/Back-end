package org.chewing.v1.model.feed

@JvmInline
value class FeedId private constructor(
    val id: String,
) {
    companion object {
        fun of(
            id: String,
        ): FeedId {
            return FeedId(id)
        }
    }
}
