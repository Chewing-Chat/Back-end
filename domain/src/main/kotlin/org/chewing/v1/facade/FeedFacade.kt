package org.chewing.v1.facade

import org.chewing.v1.implementation.feed.comment.CommentAggregator
import org.chewing.v1.implementation.feed.feed.FeedAggregator
import org.chewing.v1.model.comment.Comment
import org.chewing.v1.model.comment.UserCommentedInfo
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.service.feed.FeedCommentService
import org.chewing.v1.service.feed.FeedLikesService
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.notification.NotificationService
import org.chewing.v1.service.user.UserService
import org.springframework.stereotype.Component

@Component
class FeedFacade(
    private val feedService: FeedService,
    private val feedCommentService: FeedCommentService,
    private val feedLikeService: FeedLikesService,
    private val friendShipService: FriendShipService,
    private val userService: UserService,
    private val feedAggregator: FeedAggregator,
    private val commentAggregator: CommentAggregator,
    private val notificationService: NotificationService,
) {
    fun removesFeed(userId: String, feedIds: List<String>) {
        feedService.removes(userId, feedIds)
        feedCommentService.removes(feedIds)
        feedLikeService.unlikes(feedIds)
    }

    fun commentFeed(userId: String, feedId: String, comment: String, target: FeedTarget): String {
        val commentId = feedCommentService.comment(userId, feedId, comment, target)
        notificationService.handleCommentNotification(userId, feedId, comment)
        return commentId
    }

    fun getOwnedFeed(userId: String, feedId: String): Pair<Feed, Boolean> {
        val feed = feedService.getFeed(feedId)
        val isLiked = feedLikeService.checkLike(feedId, userId)
        return Pair(feed, isLiked)
    }

    fun getFeedComment(userId: String, feedId: String): List<Comment> {
        val comments = feedCommentService.getComment(feedId)
        val friendShips = friendShipService.getAccessFriendShipsIn(comments.map { it.userId }, userId)
        val users = userService.getUsers(friendShips.map { it.friendId })
        return feedAggregator.aggregates(comments, friendShips, users)
    }

    fun getUserCommented(userId: String): List<UserCommentedInfo> {
        val comments = feedCommentService.getOwnedComment(userId)
        val feeds = feedService.getFeeds(comments.map { it.feedId })
        val friendShips = friendShipService.getAccessFriendShipsIn(feeds.map { it.feed.userId }, userId)
        val users = userService.getUsers(friendShips.map { it.friendId })
        return commentAggregator.aggregateUserCommented(friendShips, comments, users, feeds)
    }
}
