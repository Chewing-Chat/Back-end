package org.chewing.v1.repository.feed

import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.media.Media

interface FeedDetailRepository {
    fun read(feedId: FeedId): List<FeedDetail>
    fun readsDetails(feedIds: List<FeedId>): List<FeedDetail>
    fun removes(feedIds: List<FeedId>)
    fun append(medias: List<Media>, feedId: FeedId)
    fun reads(feedIds: List<FeedId>): List<FeedDetail>
}
