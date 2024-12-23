package org.chewing.v1.implementation.feed.comment

import org.chewing.v1.model.comment.CommentInfo
import org.chewing.v1.model.comment.UserCommentedInfo
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.User
import org.springframework.stereotype.Component

@Component
class CommentAggregator {

    fun aggregateUserCommented(
        friendShips: List<FriendShip>,
        comments: List<CommentInfo>,
        users: List<User>,
        feeds: List<Feed>,
    ): List<UserCommentedInfo> {
        val feedMap = feeds.associateBy { it.feed.feedId }
        val userMap = users.associateBy { it.userId }
        val friendMap = friendShips.associateBy { it.friendId }
        return comments.mapNotNull { comment ->
            val feed = feedMap[comment.feedId]
            val user = userMap[comment.userId]
            val friend = friendMap[comment.userId]
            if (feed != null && user != null && friend != null) {
                UserCommentedInfo.of(comment, friend, user, feed)
            } else {
                null
            }
        }
    }
}
