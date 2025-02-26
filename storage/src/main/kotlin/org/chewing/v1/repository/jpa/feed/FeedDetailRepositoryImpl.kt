package org.chewing.v1.repository.jpa.feed

import org.chewing.v1.jpaentity.feed.FeedDetailJpaEntity
import org.chewing.v1.jparepository.feed.FeedDetailJpaRepository
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.model.media.Media
import org.chewing.v1.repository.feed.FeedDetailRepository
import org.chewing.v1.util.SortType
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class FeedDetailRepositoryImpl(
    private val feedDetailJpaRepository: FeedDetailJpaRepository,
) : FeedDetailRepository {
    override fun read(feedId: FeedId): List<FeedDetail> =
        feedDetailJpaRepository.findAllByFeedIdAndStatus(feedId.id, FeedStatus.ACTIVE, SortType.SMALLEST.toSort()).map { it.toFeedDetail() }

    override fun readsDetails(feedIds: List<FeedId>): List<FeedDetail> {
        val feedDetails = feedDetailJpaRepository.findByFeedIdInAndStatus(
            feedIds.map { it.id },
            FeedStatus.ACTIVE,
            SortType.SMALLEST.toSort(),
        )
        return feedDetails.map { it.toFeedDetail() }
    }

    @Transactional
    override fun removes(feedIds: List<FeedId>) {
        val details = feedDetailJpaRepository.findAllByFeedIdInAndStatus(feedIds.map { it.id }, FeedStatus.ACTIVE, SortType.SMALLEST.toSort())
        details.map { it.delete() }
        feedDetailJpaRepository.saveAll(details)
    }

    override fun append(medias: List<Media>, feedId: FeedId) {
        feedDetailJpaRepository.saveAll(FeedDetailJpaEntity.generate(medias, feedId))
    }

    override fun reads(feedIds: List<FeedId>): List<FeedDetail> =
        feedDetailJpaRepository.findAllByFeedIdInAndStatus(feedIds.map { it.id }, FeedStatus.ACTIVE, SortType.SMALLEST.toSort())
            .map { it.toFeedDetail() }
}
