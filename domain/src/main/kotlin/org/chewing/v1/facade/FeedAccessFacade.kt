package org.chewing.v1.facade

import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.service.friend.FriendShipService
import org.springframework.stereotype.Component

@Component
class FeedAccessFacade(
    private val feedService: FeedService,
    private val friendShipService: FriendShipService,
) {
    fun getFriendFeeds(userId: UserId, friendId: UserId): List<Feed> {
        friendShipService.checkAccessibleFriendShip(userId, friendId)
        return feedService.getFeeds(userId, friendId)
    }
}
