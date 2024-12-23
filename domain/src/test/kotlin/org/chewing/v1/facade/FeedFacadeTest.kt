package org.chewing.v1.facade

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.feed.comment.CommentAggregator
import org.chewing.v1.implementation.feed.feed.FeedAggregator
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.service.feed.FeedCommentService
import org.chewing.v1.service.feed.FeedLikesService
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.notification.NotificationService
import org.chewing.v1.service.user.UserService
import org.junit.jupiter.api.Test

class FeedFacadeTest {
    private val feedService: FeedService = mockk()
    private val feedCommentService: FeedCommentService = mockk()
    private val feedLikeService: FeedLikesService = mockk()
    private val friendShipService: FriendShipService = mockk()
    private val userService: UserService = mockk()
    private val notificationService: NotificationService = mockk()
    private val feedAggregator: FeedAggregator = FeedAggregator()
    private val commentAggregator: CommentAggregator = CommentAggregator()

    private val feedFacade = FeedFacade(
        feedService,
        feedCommentService,
        feedLikeService,
        friendShipService,
        userService,
        feedAggregator,
        commentAggregator,
        notificationService,
    )

    @Test
    fun `피드가 삭제되어야 함`() {
        val userId = "123"
        val feedIds = listOf("1", "2", "3")

        every { feedService.removes(userId, feedIds) } just Runs
        every { feedCommentService.removes(feedIds) } just Runs
        every { feedLikeService.unlikes(feedIds) } just Runs

        feedFacade.removesFeed(userId, feedIds)

        verify {
            feedService.removes(userId, feedIds)
            feedCommentService.removes(feedIds)
            feedLikeService.unlikes(feedIds)
        }
    }

    @Test
    fun `피드에 댓글을 달아야 함`() {
        val userId = "123"
        val feedId = "1"
        val comment = "댓글"
        val target = FeedTarget.COMMENTS
        val commentId = "testCommentId"

        every { feedCommentService.comment(userId, feedId, comment, target) } returns commentId
        every { notificationService.handleCommentNotification(userId, feedId, comment) } just Runs

        val result = feedFacade.commentFeed(userId, feedId, comment, target)

        // 피드 댓글을 달고, 댓글 알림을 처리해야 함

        assert(result == commentId)

        verify {
            feedCommentService.comment(userId, feedId, comment, target)
            notificationService.handleCommentNotification(userId, feedId, comment)
        }
    }

    @Test
    fun `소유 하고 있는 피드를 가져와야함`() {
        val userId = "123"
        val feedId = "1"
        val feed = TestDataFactory.createFeed(feedId, userId)
        val favorite = true

        every { feedService.getFeed(feedId) } returns feed
        every { feedLikeService.checkLike(feedId, userId) } returns favorite

        val result = feedFacade.getOwnedFeed(userId, feedId)

        assert(result.first == feed)
        assert(favorite == result.second)
    }

    @Test
    fun `피드 댓글을 가져와야함`() {
        val userId = "123"
        val feedId = "1"
        val commentId = "1"
        val comment = TestDataFactory.createCommentInfo(userId, commentId, feedId)
        val friendShip = TestDataFactory.createFriendShip(userId, AccessStatus.ACCESS)
        val user = TestDataFactory.createUser(friendShip.friendId)

        every { feedCommentService.getComment(feedId) } returns listOf(comment)
        every { friendShipService.getAccessFriendShipsIn(listOf(userId), userId) } returns listOf(friendShip)
        every { userService.getUsers(listOf(friendShip.friendId)) } returns listOf(user)

        val result = feedFacade.getFeedComment(userId, feedId)

        assert(result.size == 1)
        assert(result[0].comment == comment.comment)
        assert(result[0].id == comment.commentId)
        assert(result[0].writer.userId == friendShip.friendId)
        assert(result[0].writer.name == friendShip.friendName)
        assert(result[0].createAt == comment.createAt)
    }

    @Test
    fun `피드 댓글의 소유자가 친구가 아닌 경우 제외 되어야 함`() {
        val userId = "123"
        val feedId = "1"
        val commentId = "1"
        val comment = TestDataFactory.createCommentInfo(userId, commentId, feedId)

        every { feedCommentService.getComment(feedId) } returns listOf(comment)
        every { friendShipService.getAccessFriendShipsIn(listOf(userId), userId) } returns listOf()
        every { userService.getUsers(any()) } returns listOf()

        val result = feedFacade.getFeedComment(userId, feedId)

        assert(result.isEmpty())
    }

    @Test
    fun `소유자가 작성한 댓글을 가져와야함`() {
        val userId = "userId"
        val commentId = "commentId"
        val feedId = "feedId"

        val friendShip = TestDataFactory.createFriendShip(userId, AccessStatus.ACCESS)
        val comment = TestDataFactory.createCommentInfo(userId, commentId, feedId)
        val feed = TestDataFactory.createFeed(feedId, userId)
        val user = TestDataFactory.createUser(friendShip.friendId)

        every { feedCommentService.getOwnedComment(userId) } returns listOf(comment)
        every { feedService.getFeeds(listOf(feedId)) } returns listOf(feed)
        every { friendShipService.getAccessFriendShipsIn(listOf(userId), userId) } returns listOf(friendShip)
        every { userService.getUsers(listOf(friendShip.friendId)) } returns listOf(user)

        val result = feedFacade.getUserCommented(userId)

        assert(result.size == 1)
        assert(result[0].feed == feed)
        assert(result[0].comment == comment)
        assert(result[0].user == user)
    }

    @Test
    fun `소유자가 작성한 댓글을 가져와야함 - 친구가 아닌 경우`() {
        val userId = "userId"
        val commentId = "commentId"
        val feedId = "feedId"

        val comment = TestDataFactory.createCommentInfo(userId, commentId, feedId)
        val feed = TestDataFactory.createFeed(feedId, userId)

        every { feedCommentService.getOwnedComment(userId) } returns listOf(comment)
        every { feedService.getFeeds(listOf(feedId)) } returns listOf(feed)
        every { friendShipService.getAccessFriendShipsIn(listOf(userId), userId) } returns listOf()
        every { userService.getUsers(any()) } returns listOf()

        val result = feedFacade.getUserCommented(userId)

        assert(result.isEmpty())
    }
}
