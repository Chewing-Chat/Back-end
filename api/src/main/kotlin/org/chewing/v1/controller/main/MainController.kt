package org.chewing.v1.controller.main

import org.chewing.v1.dto.response.main.MainResponse
import org.chewing.v1.facade.DirectChatFacade
import org.chewing.v1.facade.FriendFacade
import org.chewing.v1.facade.FriendFeedFacade
import org.chewing.v1.facade.GroupChatFacade
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.user.UserService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/main")
class MainController(
    private val userService: UserService,
    private val friendFacade: FriendFacade,
    private val directChatFacade: DirectChatFacade,
    private val groupChatFacade: GroupChatFacade,
    private val friendFeedFacade: FriendFeedFacade,
) {
    @GetMapping("")
    fun getMainPage(
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<MainResponse> {
        val user = userService.getUser(userId, AccessStatus.ACCESS)
        val friends = friendFacade.getFriends(userId)
        val directChat = directChatFacade.processUnreadDirectChatLog(userId)
        val groupChats = groupChatFacade.processUnreadGroupChatLog(userId)
        val oneDayFeeds = friendFeedFacade.getOneDayFeeds(userId)
        return ResponseHelper.success(MainResponse.ofList(user.info, friends, directChat, groupChats, userId, oneDayFeeds))
    }
}
