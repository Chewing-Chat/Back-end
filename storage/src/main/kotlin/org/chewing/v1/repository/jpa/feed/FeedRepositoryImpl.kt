package org.chewing.v1.repository.jpa.feed

import jakarta.transaction.Transactional
import org.chewing.v1.jpaentity.feed.FeedJpaEntity
import org.chewing.v1.jparepository.feed.FeedJpaRepository
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.feed.FeedRepository
import org.chewing.v1.util.SortType
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
internal class FeedRepositoryImpl(
    private val feedJpaRepository: FeedJpaRepository,
) : FeedRepository {
    override fun read(feedId: FeedId): FeedInfo? =
        feedJpaRepository.findByFeedIdAndStatus(feedId.id, FeedStatus.ACTIVE)
            ?.toFeedInfo()

    override fun reads(userId: UserId): List<FeedInfo> =
        feedJpaRepository.findAllByUserIdAndStatus(userId.id, FeedStatus.ACTIVE, SortType.LATEST.toSort())
            .map { it.toFeedInfo() }

    @Transactional
    override fun removes(feedIds: List<FeedId>) {
        val feeds = feedJpaRepository.findByFeedIdIn(feedIds.map { it.id })
        feeds.map { it.delete() }
        feedJpaRepository.saveAll(feeds)
    }

    @Transactional
    override fun removesOwned(userId: UserId) {
        val feeds = feedJpaRepository.findAllByUserId(userId.id)
        feeds.map { it.delete() }
        feedJpaRepository.saveAll(feeds)
    }

    override fun append(userId: UserId, content: String, type: FeedType) =
        feedJpaRepository.save(FeedJpaEntity.generate(content, userId, type)).toFeedId()

    override fun isOwners(feedIds: List<FeedId>, userId: UserId): Boolean {
        return feedJpaRepository.existsByFeedIdInAndUserId(feedIds.map { it.id }, userId.id)
    }

    override fun isOwner(feedId: FeedId, userId: UserId): Boolean {
        return feedJpaRepository.existsByFeedIdAndUserId(feedId.id, userId.id)
    }

    override fun readsOneDay(targetUserIds: List<UserId>): List<FeedInfo> {
        val now = LocalDateTime.now()
        val startDate = now.minusDays(1)
        return feedJpaRepository.findAllByUserIdInAndCreatedAtAfter(
            targetUserIds.map { it.id },
            startDate,
            SortType.LATEST.toSort(),
        )
            .map { it.toFeedInfo() }
    }

    override fun update(feedId: FeedId, content: String) {
        feedJpaRepository.findById(feedId.id).ifPresent {
            it.updateContent(content)
            feedJpaRepository.save(it)
        }
    }
}
