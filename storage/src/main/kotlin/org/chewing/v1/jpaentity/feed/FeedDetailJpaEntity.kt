package org.chewing.v1.jpaentity.feed

import jakarta.persistence.*
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedDetailId
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.media.*
import org.chewing.v1.model.media.MediaType.*
import java.util.*

@Entity
@Table(name = "feed_detail", schema = "chewing")
internal class FeedDetailJpaEntity(
    @Id
    private val feedDetailId: String = UUID.randomUUID().toString(),
    private val feedIndex: Int,
    private val feedDetailUrl: String,
    @Enumerated(EnumType.STRING)
    private val feedDetailType: MediaType,
    private val feedId: String,
) {
    companion object {
        fun generate(medias: List<Media>, feedId: FeedId): List<FeedDetailJpaEntity> {
            return medias.map { media ->
                FeedDetailJpaEntity(
                    feedIndex = media.index,
                    feedDetailUrl = media.url,
                    feedDetailType = media.type,
                    feedId = feedId.id,
                )
            }
        }
    }

    fun toFeedDetail(): FeedDetail {
        return FeedDetail.of(
            feedDetailId = FeedDetailId.of(feedDetailId),
            media = Media.of(FileCategory.FEED, feedDetailUrl, feedIndex, feedDetailType),
            feedId = FeedId.of(feedId),
        )
    }
}
