
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.feed.FeedType
import java.time.format.DateTimeFormatter

sealed class OneDayFeedResponse(
    open val feedId: String,
    open val feedType: String,
    open val uploadAt: String,
    open val ownerId: String,
    open val content: String,
) {
    companion object {
        fun of(feed: Feed): OneDayFeedResponse {
            val formattedUploadTime = feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            return when (feed.feed.type) {
                FeedType.FILE -> ImageFeedResponse(
                    feedId = feed.feed.feedId.id,
                    thumbnailFileUrl = feed.feedDetails.first().media.url,
                    fileType = feed.feedDetails.first().media.type.value(),
                    uploadAt = formattedUploadTime,
                    feedType = feed.feed.type.name.lowercase(),
                    count = feed.feedDetails.size,
                    ownerId = feed.feed.userId.id,
                    content = feed.feed.content,
                )

                FeedType.TEXT_BLUE -> TextFeedResponse(
                    feedId = feed.feed.feedId.id,
                    content = feed.feed.content,
                    uploadAt = formattedUploadTime,
                    feedType = feed.feed.type.name.lowercase(),
                    ownerId = feed.feed.userId.id,
                )

                FeedType.TEXT_SKY -> TextFeedResponse(
                    feedId = feed.feed.feedId.id,
                    content = feed.feed.content,
                    uploadAt = formattedUploadTime,
                    feedType = feed.feed.type.name.lowercase(),
                    ownerId = feed.feed.userId.id,
                )
            }
        }
    }

    data class ImageFeedResponse(
        override val feedId: String,
        override val feedType: String,
        override val ownerId: String,
        val thumbnailFileUrl: String,
        val fileType: String,
        override val content: String,
        override val uploadAt: String,
        val count: Int,
    ) : OneDayFeedResponse(feedId, feedType, uploadAt, ownerId, content)

    data class TextFeedResponse(
        override val feedId: String,
        override val feedType: String,
        override val ownerId: String,
        override val content: String,
        override val uploadAt: String,
    ) : OneDayFeedResponse(feedId, feedType, uploadAt, ownerId, content)
}
