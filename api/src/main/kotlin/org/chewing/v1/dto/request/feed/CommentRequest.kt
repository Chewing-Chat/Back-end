package org.chewing.v1.dto.request.feed

class CommentRequest {
    data class Add(
        val feedId: String,
        val comment: String,
    ) {
        fun toFeedId(): String = feedId
        fun toComment(): String = comment
    }

    data class Delete(
        val commentId: String,
        val feedId: String,
    ) {
        fun toCommentId(): String = commentId
    }
}
