package org.chewing.v1.controller.feed

import org.chewing.v1.dto.response.feed.FeedIdResponse
import org.chewing.v1.dto.request.feed.FeedRequest
import org.chewing.v1.dto.response.feed.FeedResponse
import org.chewing.v1.dto.response.feed.ThumbnailFeedsResponse
import org.chewing.v1.facade.FeedAccessFacade
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.util.helper.FileHelper
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/feed")
class FeedController(
    private val feedService: FeedService,
    private val feedAccessFacade: FeedAccessFacade,
) {

    @GetMapping("/{feedId}/detail")
    fun getFeed(
        @CurrentUser userId: UserId,
        @PathVariable("feedId") feedId: String,
    ): SuccessResponseEntity<FeedResponse> {
        val feed = feedService.getFeed(FeedId.of(feedId), userId)
        return ResponseHelper.success(FeedResponse.of(feed))
    }

    @GetMapping("/owned/list")
    fun getOwnedFeedThumbnails(
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<ThumbnailFeedsResponse> {
        val feeds = feedService.getFeeds(userId, userId)
        return ResponseHelper.success(ThumbnailFeedsResponse.of(feeds))
    }

    @GetMapping("/friend/{friendId}/list")
    fun getFriendFeedThumbnails(
        @CurrentUser userId: UserId,
        @PathVariable("friendId") friendId: String,
    ): SuccessResponseEntity<ThumbnailFeedsResponse> {
        val feeds = feedAccessFacade.getFriendFeeds(userId, UserId.of(friendId))
        return ResponseHelper.success(ThumbnailFeedsResponse.of(feeds))
    }

    @DeleteMapping("")
    fun deleteFeeds(
        @CurrentUser userId: UserId,
        @RequestBody request: List<FeedRequest.Delete>,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        feedService.removes(userId, request.map { it.toFeedId() })
        return ResponseHelper.successOnly()
    }

    @PostMapping("")
    fun createFeed(
        @CurrentUser userId: UserId,
        @RequestPart("files") files: List<MultipartFile>,
        @RequestParam("content") content: String,
        @RequestParam("friendIds") friendIds: List<String>,
    ): SuccessResponseEntity<FeedIdResponse> {
        val convertFiles = FileHelper.convertMultipartFileToFileDataList(files)
        val feedId = feedService.make(userId, friendIds.map { UserId.of(it) }, convertFiles, content, FileCategory.FEED)
        return ResponseHelper.successCreate(FeedIdResponse.of(feedId))
    }
}
