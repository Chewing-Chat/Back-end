package org.chewing.v1.jpaentity.feed

import jakarta.persistence.*
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedDetailId
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.model.media.*
import java.util.*

@Entity
@Table(
    name = "feed_detail",
    schema = "chewing",
    indexes = [
        Index(name = "feed_detail_idx_feed_id_status_index", columnList = "feedId, status, index"),
    ],
)
internal class FeedDetailJpaEntity(
    @Id
    private val feedDetailId: String = UUID.randomUUID().toString(),
    private val index: Int,
    private val fileUrl: String,
    @Enumerated(EnumType.STRING)
    private val fileType: MediaType,
    private val feedId: String,
    @Enumerated(EnumType.STRING)
    private var status: FeedStatus,
) {
    companion object {
        fun generate(medias: List<Media>, feedId: FeedId): List<FeedDetailJpaEntity> {
            return medias.map { media ->
                FeedDetailJpaEntity(
                    index = media.index,
                    fileUrl = media.url,
                    fileType = media.type,
                    feedId = feedId.id,
                    status = FeedStatus.ACTIVE,
                )
            }
        }
    }

    fun toFeedDetail(): FeedDetail {
        return FeedDetail.of(
            feedDetailId = FeedDetailId.of(feedDetailId),
            media = Media.of(FileCategory.FEED, fileUrl, index, fileType),
            feedId = FeedId.of(feedId),
        )
    }

    fun delete() {
        status = FeedStatus.DELETED
    }
}
