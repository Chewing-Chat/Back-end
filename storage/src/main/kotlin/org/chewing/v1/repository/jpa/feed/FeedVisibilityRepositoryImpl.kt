package org.chewing.v1.repository.jpa.feed

import jakarta.transaction.Transactional
import org.chewing.v1.jpaentity.feed.FeedVisibilityEntity
import org.chewing.v1.jpaentity.feed.FeedVisibilityId
import org.chewing.v1.jparepository.feed.FeedVisibilityJpaRepository
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.feed.FeedVisibilityRepository
import org.springframework.stereotype.Repository

@Repository
internal class FeedVisibilityRepositoryImpl(
    private val feedVisibilityJpaRepository: FeedVisibilityJpaRepository,
) : FeedVisibilityRepository {
    override fun append(feedId: String, targetUserIds: List<UserId>) {
        feedVisibilityJpaRepository.saveAll(targetUserIds.map { FeedVisibilityEntity.generate(feedId, it) })
    }

    override fun isVisible(feedId: String, userId: UserId): Boolean {
        return feedVisibilityJpaRepository.existsById(
            FeedVisibilityId.of(
                feedId = feedId,
                userId = userId,
            ),
        )
    }

    override fun readVisibleFeedIds(userId: UserId, feedIds: List<String>): List<String> {
        val feedVisibilityIds = feedIds.map { FeedVisibilityId(feedId = it, userId = userId.id) }
        return feedVisibilityJpaRepository.findAllByIdIn(feedVisibilityIds).map { it.getFeedId() }
    }

    @Transactional
    override fun removes(feedIds: List<String>) {
        feedVisibilityJpaRepository.deleteAllByIdFeedIdIn(feedIds)
    }
}
