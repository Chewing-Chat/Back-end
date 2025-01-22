package org.chewing.v1.controller.feed

import org.chewing.v1.dto.response.feed.FeedIdResponse
import org.chewing.v1.dto.request.feed.FeedRequest
import org.chewing.v1.dto.response.feed.FeedResponse
import org.chewing.v1.dto.response.feed.ThumbnailFeedsResponse
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.util.helper.FileHelper
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/feed")
class FeedController(
    private val feedService: FeedService,
) {

    @GetMapping("/{feedId}/detail")
    fun getFeed(
        @RequestAttribute("userId") userId: String,
        @PathVariable("feedId") feedId: String,
    ): SuccessResponseEntity<FeedResponse> {
        val feed = feedService.getFeed(FeedId.of(feedId), UserId.of(userId))
        return ResponseHelper.success(FeedResponse.of(feed))
    }

    @GetMapping("/owned/list")
    fun getOwnedFeedThumbnails(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<ThumbnailFeedsResponse> {
        val feeds = feedService.getFeeds(UserId.of(userId), UserId.of(userId))
        return ResponseHelper.success(ThumbnailFeedsResponse.of(feeds))
    }

    @GetMapping("/friend/{friendId}/list")
    fun getFriendFeedThumbnails(
        @RequestAttribute("userId") userId: String,
        @PathVariable("friendId") friendId: String,
    ): SuccessResponseEntity<ThumbnailFeedsResponse> {
        val feeds = feedService.getFeeds(UserId.of(userId), UserId.of(friendId))
        return ResponseHelper.success(ThumbnailFeedsResponse.of(feeds))
    }

    @DeleteMapping("")
    fun deleteFeeds(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: List<FeedRequest.Delete>,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        feedService.removes(UserId.of(userId), request.map { it.toFeedId() })
        return ResponseHelper.successOnly()
    }

    @PostMapping("")
    fun createFeed(
        @RequestAttribute("userId") userId: String,
        @RequestPart("files") files: List<MultipartFile>,
        @RequestParam("content") content: String,
        @RequestParam("friendIds") friendIds: List<String>,
    ): SuccessResponseEntity<FeedIdResponse> {
        val convertFiles = FileHelper.convertMultipartFileToFileDataList(files)
        val feedId = feedService.make(UserId.of(userId), friendIds.map { UserId.of(it) }, convertFiles, content, FileCategory.FEED)
        return ResponseHelper.successCreate(FeedIdResponse.of(feedId))
    }
}
