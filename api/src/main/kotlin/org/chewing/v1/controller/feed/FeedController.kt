package org.chewing.v1.controller.feed

import org.chewing.v1.dto.response.feed.FeedIdResponse
import org.chewing.v1.dto.request.feed.FeedRequest
import org.chewing.v1.dto.response.feed.FeedResponse
import org.chewing.v1.dto.response.feed.ThumbnailFeedsResponse
import org.chewing.v1.model.media.FileCategory
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
        val feed = feedService.getFeed(feedId, userId)
        return ResponseHelper.success(FeedResponse.of(feed))
    }

    @GetMapping("/owned/list")
    fun getOwnedFeedsThumbnail(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<ThumbnailFeedsResponse> {
        val feeds = feedService.getFeeds(userId, userId)
        return ResponseHelper.success(ThumbnailFeedsResponse.of(feeds))
    }

    @GetMapping("/friend/{friendId}/list")
    fun getFriendFeedsThumbnail(
        @RequestAttribute("userId") userId: String,
        @PathVariable("friendId") friendId: String,
    ): SuccessResponseEntity<ThumbnailFeedsResponse> {
        val feeds = feedService.getFeeds(userId, friendId)
        return ResponseHelper.success(ThumbnailFeedsResponse.of(feeds))
    }

    @DeleteMapping("")
    fun deleteFeeds(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: List<FeedRequest.Delete>,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        feedService.removes(userId, request.map { it.toFeedId() })
        return ResponseHelper.successOnly()
    }

    @PostMapping("")
    fun createFeed(
        @RequestAttribute("userId") userId: String,
        @RequestPart("files") files: List<MultipartFile>,
        @RequestParam("topic") topic: String,
        @RequestParam("friendIds") friendIds: List<String>,
    ): SuccessResponseEntity<FeedIdResponse> {
        val convertFiles = FileHelper.convertMultipartFileToFileDataList(files)
        val feedId = feedService.make(userId, friendIds, convertFiles, topic, FileCategory.FEED)
        return ResponseHelper.successCreate(FeedIdResponse(feedId))
    }
}
