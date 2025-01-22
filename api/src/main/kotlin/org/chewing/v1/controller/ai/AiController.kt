package org.chewing.v1.controller.ai

import org.chewing.v1.facade.AiFacade
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ai")
class AiController(
    private val aiFacade: AiFacade,
) {
//    @GetMapping("/friend/{friendId}/summary")
//    fun getFriendSummary(
//        @RequestAttribute("userId") userId: String,
//        @PathVariable("friendId") friendId: String,
//        @RequestParam("targetDate") dateTarget: DateTarget,
//    ): SuccessResponseEntity<AiResponse> {
//        val result = aiFacade.getAiRecentSummary(UserId.of(userId), UserId.of(friendId), dateTarget)
//        return ResponseHelper.success(AiResponse.from(result))
//    }
//
//    @PostMapping("/chat/search")
//    fun searchChat(
//        @RequestBody request: AiRequest.ChatSearch,
//    ): SuccessResponseEntity<ChatLogResponse> {
//        val result = aiFacade.getAiSearchChat(request.chatRoomId, request.prompt)
//        return ResponseHelper.success(ChatLogResponse.from(result))
//    }
//
//    @PostMapping("/schedule")
//    fun createSchedule(
//        @RequestAttribute("userId") userId: String,
//        @RequestBody request: AiRequest.Schedule,
//    ): SuccessResponseEntity<ScheduleIdResponse> {
//        val scheduleId = aiFacade.createAiSchedule(userId, request.prompt)
//        return ResponseHelper.successCreate(ScheduleIdResponse(scheduleId))
//    }
}
