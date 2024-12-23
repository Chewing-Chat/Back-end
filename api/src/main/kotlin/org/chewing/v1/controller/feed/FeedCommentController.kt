package org.chewing.v1.controller.feed

import org.chewing.v1.dto.request.feed.CommentIdResponse
import org.chewing.v1.dto.request.feed.CommentRequest
import org.chewing.v1.dto.response.comment.FeedFriendCommentedResponse
import org.chewing.v1.dto.response.comment.MyCommentResponse
import org.chewing.v1.facade.FeedFacade
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.feed.FeedCommentService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/feed")
class FeedCommentController(
    private val feedCommentService: FeedCommentService,
    private val feedFacade: FeedFacade,
) {
    @PostMapping("/comment")
    fun addFeedComment(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: CommentRequest.Add,
    ): SuccessResponseEntity<CommentIdResponse> {
        val commentId = feedFacade.commentFeed(
            userId,
            request.toFeedId(),
            request.toComment(),
            FeedTarget.COMMENTS,
        )
        return ResponseHelper.successCreate(CommentIdResponse(commentId))
    }

    @DeleteMapping("/comment")
    fun deleteFeedComment(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: List<CommentRequest.Delete>,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        feedCommentService.remove(
            userId,
            request.map { it.toCommentId() },
            FeedTarget.UNCOMMENTS,
        )
        // 삭제 완료 응답 200 반환
        return ResponseHelper.successOnly()
    }

    @GetMapping("/{feedId}/comment")
    fun getFeedComments(
        @RequestAttribute("userId") userId: String,
        @PathVariable("feedId") feedId: String,
    ): SuccessResponseEntity<FeedFriendCommentedResponse> {
        val friendComment = feedFacade.getFeedComment(userId, feedId)
        // 성공 응답 200 반환
        return ResponseHelper.success(FeedFriendCommentedResponse.of(friendComment))
    }

    @GetMapping("/my/comment")
    fun getMyCommentedFeed(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<MyCommentResponse> {
        val myCommentedInfo = feedFacade.getUserCommented(userId)
        // 성공 응답 200 반환
        return ResponseHelper.success(MyCommentResponse.of(myCommentedInfo))
    }
}
