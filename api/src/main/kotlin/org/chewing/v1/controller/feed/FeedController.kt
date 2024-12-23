package org.chewing.v1.controller.feed

import org.chewing.v1.dto.request.feed.FeedIdResponse
import org.chewing.v1.dto.request.feed.FeedRequest
import org.chewing.v1.dto.response.feed.FeedsResponse
import org.chewing.v1.dto.response.feed.FriendFeedResponse
import org.chewing.v1.dto.response.feed.OwnedFeedResponse
import org.chewing.v1.facade.FeedFacade
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.util.helper.FileHelper
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
class FeedController(
    private val feedService: FeedService,
    private val feedFacade: FeedFacade,
) {
    @GetMapping("/friend/{friendId}/feed/list")
    fun getFriendFeeds(
        @RequestAttribute("userId") userId: String,
        @PathVariable("friendId") targetUserId: String,
    ): SuccessResponseEntity<FeedsResponse> {
        val feeds = feedService.getOwnedFeeds(targetUserId, FeedStatus.NOT_HIDDEN)
        // 성공 응답 200 반환
        return ResponseHelper.success(FeedsResponse.of(feeds))
    }

    @GetMapping("/my/feed/list")
    fun getOwnedFeeds(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<FeedsResponse> {
        val feeds = feedService.getOwnedFeeds(userId, FeedStatus.NOT_HIDDEN)
        // 성공 응답 200 반환
        return ResponseHelper.success(FeedsResponse.of(feeds))
    }

    @GetMapping("/friend/feed/{feedId}/detail")
    fun getFriendFeed(
        @RequestAttribute("userId") userId: String,
        @PathVariable("feedId") feedId: String,
    ): SuccessResponseEntity<FriendFeedResponse> {
        val (feed, isLiked) = feedFacade.getOwnedFeed(userId, feedId)
        // 성공 응답 200 반환
        return ResponseHelper.success(FriendFeedResponse.of(feed, isLiked))
    }

    @GetMapping("/my/feed/{feedId}/detail")
    fun getOwnedFeed(
        @RequestAttribute("userId") userId: String,
        @PathVariable("feedId") feedId: String,
    ): SuccessResponseEntity<OwnedFeedResponse> {
        val (feed, isLiked) = feedFacade.getOwnedFeed(userId, feedId)
        // 성공 응답 200 반환
        return ResponseHelper.success(OwnedFeedResponse.of(feed, isLiked))
    }

    @GetMapping("/feed/hide")
    fun getHiddenFeeds(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<FeedsResponse> {
        val feeds = feedService.getOwnedFeeds(userId, FeedStatus.HIDDEN)
        // 성공 응답 200 반환
        return ResponseHelper.success(FeedsResponse.of(feeds))
    }

    @PostMapping("/feed/hide")
    fun hideFeeds(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: List<FeedRequest.Hide>,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        feedService.changeHide(userId, request.map { it.toFeedId() }, FeedTarget.HIDE)
        // 성공 응답 200 반환
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("/feed/hide")
    fun unHideFeeds(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: List<FeedRequest.Hide>,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        feedService.changeHide(userId, request.map { it.toFeedId() }, FeedTarget.UNHIDE)
        // 성공 응답 200 반환
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("/feed")
    fun deleteFeeds(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: List<FeedRequest.Delete>,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        feedFacade.removesFeed(userId, request.map { it.toFeedId() })
        // 삭제 완료 응답 200 반환
        return ResponseHelper.successOnly()
    }

    @PostMapping("/feed")
    fun createFeed(
        @RequestAttribute("userId") userId: String,
        @RequestPart("files") files: List<MultipartFile>,
        @RequestParam("topic") topic: String,
    ): SuccessResponseEntity<FeedIdResponse> {
        val convertFiles = FileHelper.convertMultipartFileToFileDataList(files)
        val feedId = feedService.make(userId, convertFiles, topic, FileCategory.FEED)
        // 생성 완료 응답 201 반환
        return ResponseHelper.successCreate(FeedIdResponse(feedId))
    }
}
