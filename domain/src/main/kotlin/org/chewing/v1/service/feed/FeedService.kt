package org.chewing.v1.service.feed

import org.chewing.v1.implementation.feed.FeedAppender
import org.chewing.v1.implementation.feed.FeedEnricher
import org.chewing.v1.implementation.feed.FeedReader
import org.chewing.v1.implementation.feed.FeedRemover
import org.chewing.v1.implementation.feed.FeedValidator
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.model.feed.*
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class FeedService(
    private val feedReader: FeedReader,
    private val feedAppender: FeedAppender,
    private val feedValidator: FeedValidator,
    private val fileHandler: FileHandler,
    private val feedEnricher: FeedEnricher,
    private val feedRemover: FeedRemover,
) {
    fun getFeed(feedId: FeedId, userId: UserId): Feed {
        feedValidator.isFeedVisible(feedId, userId)
        val feed = feedReader.readInfo(feedId)
        val feedDetails = feedReader.readDetails(feedId)
        return Feed.of(feed, feedDetails)
    }

    fun getFeeds(userId: UserId, targetUserId: UserId): List<Feed> {
        val feeds = feedReader.readsInfo(targetUserId)
        val visibleFeedIds = feedReader.readVisibleFeedIds(userId, feeds.map { it.feedId })
        val feedsDetails = feedReader.readsThumbnail(visibleFeedIds)
        return feedEnricher.enriches(feeds, visibleFeedIds, feedsDetails)
    }

    fun removes(userId: UserId, feedIds: List<FeedId>) {
        feedValidator.isFeedsOwner(feedIds, userId)
        val oldMedias = feedRemover.removes(feedIds)
        fileHandler.handleOldFiles(oldMedias)
    }

    fun make(
        userId: UserId,
        targetFriends: List<UserId>,
        files: List<FileData>,
        content: String,
        category: FileCategory,
    ): FeedId {
        val medias = fileHandler.handleNewFiles(userId, files, category)
        val feedId = feedAppender.append(medias, userId, content)
        val targetUserIds = targetFriends.plus(userId)
        feedAppender.appendVisibility(feedId, targetUserIds)
        return feedId
    }
}
