package org.chewing.v1.repository.jpa.feed

import jakarta.transaction.Transactional
import org.chewing.v1.jpaentity.feed.FeedJpaEntity
import org.chewing.v1.jparepository.feed.FeedJpaRepository
import org.chewing.v1.model.ai.DateTarget
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.feed.FeedRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
internal class FeedRepositoryImpl(
    private val feedJpaRepository: FeedJpaRepository,
) : FeedRepository {
    override fun read(feedId: FeedId): FeedInfo? =
        feedJpaRepository.findById(feedId.id).map { it.toFeedInfo() }.orElse(null)

    override fun reads(userId: UserId): List<FeedInfo> =
        feedJpaRepository.findAllByUserIdOrderByCreatedAtAsc(userId.id).map { it.toFeedInfo() }

    override fun readsFriendBetween(userId: UserId, dateTarget: DateTarget): List<FeedInfo> {
        val now = LocalDateTime.now()
        val startDate = when (dateTarget) {
            DateTarget.WEEKLY -> now.minusWeeks(1)
            DateTarget.MONTHLY -> now.minusMonths(1)
        }
        return feedJpaRepository.findAllByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(userId.id, startDate)
            .map { it.toFeedInfo() }
    }

    override fun removes(feedIds: List<FeedId>) {
        feedJpaRepository.deleteAllById(feedIds.map { it.id })
    }

    @Transactional
    override fun removesOwned(userId: UserId) {
        feedJpaRepository.deleteAllByUserId(userId.id)
    }

    override fun append(userId: UserId, content: String) =
        feedJpaRepository.save(FeedJpaEntity.generate(content, userId)).toFeedId()

    override fun isOwners(feedIds: List<FeedId>, userId: UserId): Boolean {
        return feedJpaRepository.existsByFeedIdInAndUserId(feedIds.map { it.id }, userId.id)
    }
}
