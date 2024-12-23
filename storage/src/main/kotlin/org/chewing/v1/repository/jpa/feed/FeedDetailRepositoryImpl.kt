package org.chewing.v1.repository.jpa.feed

import org.chewing.v1.jpaentity.feed.FeedDetailJpaEntity
import org.chewing.v1.jparepository.feed.FeedDetailJpaRepository
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.media.Media
import org.chewing.v1.repository.feed.FeedDetailRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class FeedDetailRepositoryImpl(
    private val feedDetailJpaRepository: FeedDetailJpaRepository,
) : FeedDetailRepository {
    override fun read(feedId: String): List<FeedDetail> = feedDetailJpaRepository.findAllByFeedIdOrderByFeedIndex(feedId).map { it.toFeedDetail() }

    override fun readsFirstIndex(feedIds: List<String>): List<FeedDetail> {
        val feedDetails = feedDetailJpaRepository.findByFeedIdInAndFeedIndex(feedIds, 0)
        return feedDetails.map { it.toFeedDetail() }
    }

    @Transactional
    override fun removes(feedIds: List<String>): List<Media> {
        val details = feedDetailJpaRepository.findAllByFeedIdIn(feedIds.map { it }).map { it.toFeedDetail() }
        feedDetailJpaRepository.deleteAllByFeedIdIn(feedIds.map { it })
        return details.map { it.media }
    }

    override fun append(medias: List<Media>, feedId: String) {
        feedDetailJpaRepository.saveAll(FeedDetailJpaEntity.generate(medias, feedId))
    }

    override fun reads(feedIds: List<String>): List<FeedDetail> = feedDetailJpaRepository.findAllByFeedIdInOrderByFeedIndexAsc(feedIds).map { it.toFeedDetail() }
}
