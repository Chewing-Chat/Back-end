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
    private val feedHandler: FeedHandler,
    private val feedAppender: FeedAppender,
    private val feedValidator: FeedValidator,
    private val fileHandler: FileHandler,
    private val feedEnricher: FeedEnricher,
    private val feedRemover: FeedRemover,
) {
    // 피드를 가져옴
    fun getFeed(feedId: String): Feed {
        val feed = feedReader.readInfo(feedId)
        val feedDetails = feedReader.readDetails(feedId)
        return Feed.of(feed, feedDetails)
    }

    fun getFeeds(feedsId: List<String>): List<Feed> {
        val feeds = feedReader.readsInfo(feedsId)
        val feedsDetails = feedReader.readsMainDetails(feedsId)
        return feedEnricher.enriches(feeds, feedsDetails)
    }

    fun getOwnedFeeds(targetUserId: String, feedStatus: FeedStatus): List<Feed> {
        val feeds = feedReader.readsOwnedInfo(targetUserId, feedStatus)
        val feedsDetail = feedReader.readsMainDetails(feeds.map { it.feedId })
        return feedEnricher.enriches(feeds, feedsDetail)
    }

    fun getFriendFulledFeeds(friendId: String, dateTarget: DateTarget): List<Feed> {
        val feeds = feedReader.readsFriendBetween(friendId, dateTarget)
        val feedsDetail = feedReader.readsDetails(feeds.map { it.feedId })
        return feedEnricher.enriches(feeds, feedsDetail)
    }

    fun removes(userId: String, feedIds: List<String>) {
        feedValidator.isFeedsOwner(feedIds, userId)
        val oldMedias = feedRemover.removes(feedIds)
        fileHandler.handleOldFiles(oldMedias)
    }

    fun changeHide(userId: String, feedIds: List<String>, target: FeedTarget) {
        feedValidator.isFeedsOwner(feedIds, userId)
        feedHandler.lockFeedHides(feedIds, target)
    }

    fun make(userId: String, files: List<FileData>, topic: String, category: FileCategory): String {
        val medias = fileHandler.handleNewFiles(userId, files, category)
        return feedAppender.append(medias, userId, topic)
    }
}
