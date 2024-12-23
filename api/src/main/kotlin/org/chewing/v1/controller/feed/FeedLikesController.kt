package org.chewing.v1.controller.feed

import org.chewing.v1.dto.request.feed.LikesRequest
import org.chewing.v1.response.SuccessCreateResponse
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.feed.FeedLikesService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/feed/likes")
class FeedLikesController(
    private val feedLikesService: FeedLikesService,
) {
    @PostMapping("")
    fun addFeedLikes(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: LikesRequest.Add,
    ): SuccessResponseEntity<SuccessCreateResponse> {
        val feedId = request.toFeedId()
        feedLikesService.like(userId, feedId, request.toTarget())
        // 생성 완료 응답 201 반환
        return ResponseHelper.successCreateOnly()
    }

    @DeleteMapping("")
    fun deleteFeedLikes(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: LikesRequest.Delete,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val feedId = request.toFeedId()
        feedLikesService.unlike(userId, feedId, request.toUpdateType())
        // 삭제 완료 응답 200 반환
        return ResponseHelper.successOnly()
    }
}
