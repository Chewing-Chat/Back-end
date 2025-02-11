package org.chewing.v1.facade

import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.service.friend.FriendShipService
import org.springframework.stereotype.Component

@Component
class FriendFeedFacade(
    private val feedService: FeedService,
    private val friendShipService: FriendShipService,
) {
    fun getFriendFeeds(userId: UserId, friendId: UserId): List<Feed> {
        friendShipService.checkAccessibleFriendShip(userId, friendId)
        return feedService.getFeeds(userId, friendId)
    }
    fun getOneDayFeeds(userId: UserId): List<Feed> {
        val friendShips = friendShipService.getFavoriteFriendShips(userId)
        val friendIds = friendShips.map { it.friendId }
        return feedService.getOneDayFeeds(userId, friendIds)
    }
}
