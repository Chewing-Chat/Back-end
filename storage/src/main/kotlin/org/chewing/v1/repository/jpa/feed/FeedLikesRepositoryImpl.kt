package org.chewing.v1.repository.jpa.feed

import org.chewing.v1.jpaentity.user.FeedLikeId
import org.chewing.v1.jpaentity.user.UserFeedLikesJpaEntity
import org.chewing.v1.jparepository.feed.FeedLikesJpaRepository
import org.chewing.v1.repository.feed.FeedLikesRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class FeedLikesRepositoryImpl(
    private val feedLikesRepository: FeedLikesJpaRepository,
) : FeedLikesRepository {
    override fun likes(feedId: String, userId: String) {
        val userFeedJpaEntity = UserFeedLikesJpaEntity.fromUserFeed(userId, feedId)
        feedLikesRepository.save(userFeedJpaEntity)
    }

    override fun unlikes(feedId: String, userId: String) {
        feedLikesRepository.deleteById(FeedLikeId(userId, feedId))
    }

    @Transactional
    override fun unlikeAll(feedIds: List<String>) {
        feedLikesRepository.deleteAllByFeedLikeIdFeedIdIn(feedIds)
    }

    override fun checkLike(feedId: String, userId: String): Boolean =
        feedLikesRepository.existsById(FeedLikeId(userId, feedId))
}
