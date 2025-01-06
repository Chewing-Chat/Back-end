package org.chewing.v1.repository.feed

import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.media.Media

interface FeedDetailRepository {
    fun read(feedId: String): List<FeedDetail>
    fun readsFirstIndex(feedIds: List<String>): List<FeedDetail>
    fun removes(feedIds: List<String>): List<Media>
    fun append(medias: List<Media>, feedId: String)
    fun reads(feedIds: List<String>): List<FeedDetail>
}
