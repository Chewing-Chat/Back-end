package org.chewing.v1.controller.main

import org.chewing.v1.dto.response.main.MainResponse
import org.chewing.v1.facade.MainFacade
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/main")
class MainController(
    private val mainFacade: MainFacade,
) {
    @GetMapping("")
    fun getMainPage(
        @RequestAttribute("userId") userId: String,
        @RequestParam("sort") sort: FriendSortCriteria,
    ): SuccessResponseEntity<MainResponse> {
        val (user, userStatus, friends) = mainFacade.getMainPage(userId, sort)
        // 성공 응답 200 반환
        return ResponseHelper.success(MainResponse.ofList(user, userStatus, friends))
    }
}
