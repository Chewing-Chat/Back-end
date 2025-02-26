package org.chewing.v1.implementation.feed

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.feed.FeedDetailRepository
import org.chewing.v1.repository.feed.FeedRepository
import org.chewing.v1.repository.feed.FeedVisibilityRepository
import org.springframework.stereotype.Component

@Component
class FeedReader(
    private val feedRepository: FeedRepository,
    private val feedDetailRepository: FeedDetailRepository,
    private val feedVisibilityRepository: FeedVisibilityRepository,
) {
    fun readInfo(feedId: FeedId): FeedInfo =
        feedRepository.read(feedId) ?: throw NotFoundException(ErrorCode.FEED_NOT_FOUND)

    fun readInfos(userId: UserId): List<FeedInfo> = feedRepository.reads(userId)

    fun readsOneDayInfos(targetUserIds: List<UserId>): List<FeedInfo> = feedRepository.readsOneDay(targetUserIds)

    fun readVisibleFeedIds(userId: UserId, feedIds: List<FeedId>): List<FeedId> =
        feedVisibilityRepository.readVisibleFeedIds(userId, feedIds)

    fun readDetails(feedId: FeedId): List<FeedDetail> = feedDetailRepository.read(feedId)

    fun readsDetails(feedIds: List<FeedId>): List<FeedDetail> = feedDetailRepository.readsDetails(feedIds)
}
