package org.chewing.v1.repository.jpa.feed

import jakarta.transaction.Transactional
import org.chewing.v1.jpaentity.feed.FeedJpaEntity
import org.chewing.v1.jparepository.feed.FeedJpaRepository
import org.chewing.v1.model.ai.DateTarget
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.repository.feed.FeedRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
internal class FeedRepositoryImpl(
    private val feedJpaRepository: FeedJpaRepository,
) : FeedRepository {
    override fun read(feedId: String): FeedInfo? =
        feedJpaRepository.findById(feedId).map { it.toFeedInfo() }.orElse(null)

    override fun reads(feedIds: List<String>): List<FeedInfo> =
        feedJpaRepository.findAllByFeedIdInOrderByCreatedAtAsc(feedIds.map { it }).map { it.toFeedInfo() }

    override fun readsOwned(userId: String, feedStatus: FeedStatus): List<FeedInfo> = when (feedStatus) {
        FeedStatus.HIDDEN -> feedJpaRepository.findAllByUserIdAndHideTrueOrderByCreatedAtAsc(userId)
            .map { it.toFeedInfo() }

        FeedStatus.NOT_HIDDEN -> feedJpaRepository.findAllByUserIdAndHideFalseOrderByCreatedAtAsc(userId)
            .map { it.toFeedInfo() }

        FeedStatus.ALL -> feedJpaRepository.findAllByUserIdOrderByCreatedAtAsc(userId).map { it.toFeedInfo() }
    }

    override fun readsFriendBetween(userId: String, dateTarget: DateTarget): List<FeedInfo> {
        val now = LocalDateTime.now()
        val startDate = when (dateTarget) {
            DateTarget.WEEKLY -> now.minusWeeks(1)
            DateTarget.MONTHLY -> now.minusMonths(1)
        }
        return feedJpaRepository.findAllByUserIdAndHideTrueAndCreatedAtAfterOrderByCreatedAtAsc(userId, startDate)
            .map { it.toFeedInfo() }
    }

    override fun update(feedId: String, target: FeedTarget): String? =
        feedJpaRepository.findById(feedId).orElse(null)?.let {
            when (target) {
                FeedTarget.LIKES -> it.likes()
                FeedTarget.UNLIKES -> it.unLikes()
                FeedTarget.COMMENTS -> it.comments()
                FeedTarget.UNCOMMENTS -> it.unComments()
                FeedTarget.HIDE -> it.hide()
                FeedTarget.UNHIDE -> it.unHide()
            }
            feedJpaRepository.save(it)
            feedId
        }

    override fun removes(feedIds: List<String>) {
        feedJpaRepository.deleteAllById(feedIds)
    }

    @Transactional
    override fun removesOwned(userId: String) {
        feedJpaRepository.deleteAllByUserId(userId)
    }

    override fun append(userId: String, topic: String): String =
        feedJpaRepository.save(FeedJpaEntity.generate(topic, userId)).toFeedId()
}
