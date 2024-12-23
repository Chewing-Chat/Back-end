package org.chewing.v1.dto.response.comment

import org.chewing.v1.dto.response.feed.MainFeedResponse
import org.chewing.v1.dto.response.friend.FriendInfoResponse
import org.chewing.v1.model.comment.CommentInfo
import org.chewing.v1.model.comment.UserCommentedInfo
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.User
import java.time.format.DateTimeFormatter

data class MyCommentResponse(
    val myComments: List<MyCommentFeedResponse>,
) {
    companion object {
        fun of(feedWithComments: List<UserCommentedInfo>): MyCommentResponse {
            // Feed ID로 그룹화
            val feedMap = feedWithComments.groupBy { it.feed.feed.feedId }

            return MyCommentResponse(
                feedMap.map { (_, commentsPerFeed) ->
                    val feed = commentsPerFeed.first().feed
                    val friendShip = commentsPerFeed.first().friendShip
                    val user = commentsPerFeed.first().user
                    val comments = commentsPerFeed.map { it.comment }

                    MyCommentFeedResponse.of(feed, friendShip, user, comments)
                },
            )
        }
    }

    data class MyCommentFeedResponse(
        val friend: FriendInfoResponse,
        val feed: MainFeedResponse,
        val comments: List<CommentResponse>,
    ) {
        companion object {
            fun of(
                feed: Feed,
                friendShip: FriendShip,
                user: User,
                comments: List<CommentInfo>,
            ): MyCommentFeedResponse {
                return MyCommentFeedResponse(
                    FriendInfoResponse.of(
                        friendId = friendShip.friendId,
                        userName = friendShip.friendName,
                        imageUrl = user.image.url,
                        imageType = user.image.type.value().lowercase(),
                        access = user.status,
                    ),
                    MainFeedResponse.of(feed),
                    comments.map { CommentResponse.of(it) },
                )
            }
        }

        data class CommentResponse(
            val commentId: String,
            val comment: String,
            val commentTime: String,
        ) {
            companion object {
                fun of(
                    comment: CommentInfo,
                ): CommentResponse {
                    return CommentResponse(
                        commentId = comment.commentId,
                        comment = comment.comment,
                        commentTime = comment.createAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")),
                    )
                }
            }
        }
    }
}
