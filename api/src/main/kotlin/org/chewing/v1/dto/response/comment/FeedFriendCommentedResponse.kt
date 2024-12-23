package org.chewing.v1.dto.response.comment

import org.chewing.v1.dto.response.friend.FriendInfoResponse
import org.chewing.v1.model.comment.Comment

data class FeedFriendCommentedResponse(
    val comments: List<FriendCommentResponse>,
) {
    companion object {
        fun of(
            comments: List<Comment>,
        ): FeedFriendCommentedResponse {
            return FeedFriendCommentedResponse(
                comments = comments.map { FriendCommentResponse.of(it) },
            )
        }
    }

    data class FriendCommentResponse(
        val friend: FriendInfoResponse,
        val comment: CommentResponse,
    ) {
        companion object {
            fun of(
                friendComment: Comment,
            ): FriendCommentResponse {
                return FriendCommentResponse(
                    friend = FriendInfoResponse.of(
                        friendId = friendComment.writer.userId,
                        userName = friendComment.writer.name,
                        imageUrl = friendComment.writer.image.url,
                        access = friendComment.writer.status,
                        imageType = friendComment.writer.image.type.value().lowercase(),
                    ),
                    comment = CommentResponse.of(friendComment),
                )
            }
        }
    }
}
