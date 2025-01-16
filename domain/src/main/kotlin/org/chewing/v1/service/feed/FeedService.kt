package org.chewing.v1.service.feed

import org.chewing.v1.implementation.feed.feed.*
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.model.ai.DateTarget
import org.chewing.v1.model.feed.*
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.FileData
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
    fun getFeed(feedId: String, userId: String): Feed {
        feedValidator.isFeedVisible(feedId, userId)
        val feed = feedReader.readInfo(feedId)
        val feedDetails = feedReader.readDetails(feedId)
        return Feed.of(feed, feedDetails)
    }

    fun getFeeds(userId: String, targetUserId: String): List<Feed> {
        val feeds = feedReader.readsInfo(targetUserId)
        val visibleFeedIds = feedReader.readVisibleFeedIds(userId, feeds.map { it.feedId })
        val feedsDetails = feedReader.readsThumbnail(visibleFeedIds)
        return feedEnricher.enriches(feeds, visibleFeedIds, feedsDetails)
    }

    fun getFriendFulledFeeds(userId: String, friendId: String, dateTarget: DateTarget): List<Feed> {
        val feeds = feedReader.readsFriendBetween(friendId, dateTarget)
        val visibleFeedIds = feedReader.readVisibleFeedIds(userId, feeds.map { it.feedId })
        val feedsDetail = feedReader.readsDetails(visibleFeedIds)
        return feedEnricher.enriches(feeds, visibleFeedIds, feedsDetail)
    }

    fun removes(userId: String, feedIds: List<String>) {
        feedValidator.isFeedsOwner(feedIds, userId)
        val oldMedias = feedRemover.removes(feedIds)
        fileHandler.handleOldFiles(oldMedias)
    }

    fun make(
        userId: String,
        targetFriends: List<String>,
        files: List<FileData>,
        content: String,
        category: FileCategory,
    ): String {
        val medias = fileHandler.handleNewFiles(userId, files, category)
        val feedId = feedAppender.append(medias, userId, content)
        val targetUserIds = targetFriends.plus(userId)
        feedAppender.appendVisibility(feedId, targetUserIds)
        return feedId
    }
}
