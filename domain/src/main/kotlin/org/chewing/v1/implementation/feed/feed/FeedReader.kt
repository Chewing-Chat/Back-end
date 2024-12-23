package org.chewing.v1.implementation.feed.feed

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.ai.DateTarget
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.repository.feed.FeedDetailRepository
import org.chewing.v1.repository.feed.FeedRepository
import org.springframework.stereotype.Component

@Component
class FeedReader(
    private val feedRepository: FeedRepository,
    private val feedDetailRepository: FeedDetailRepository,
) {
    fun readInfo(feedId: String): FeedInfo = feedRepository.read(feedId) ?: throw NotFoundException(ErrorCode.FEED_NOT_FOUND)

    fun readsOwnedInfo(userId: String, feedStatus: FeedStatus): List<FeedInfo> = feedRepository.readsOwned(userId, feedStatus)

    fun readsInfo(feedIds: List<String>): List<FeedInfo> = feedRepository.reads(feedIds)

    fun readDetails(feedId: String): List<FeedDetail> = feedDetailRepository.read(feedId)

    fun readsMainDetails(feedIds: List<String>): List<FeedDetail> = feedDetailRepository.readsFirstIndex(feedIds)

    fun readsDetails(feedIds: List<String>): List<FeedDetail> = feedDetailRepository.reads(feedIds)
    fun readsFriendBetween(userId: String, dateTarget: DateTarget): List<FeedInfo> = feedRepository.readsFriendBetween(userId, dateTarget)
}
