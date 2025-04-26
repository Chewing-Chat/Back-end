package org.chewing.v1.controller.ai

import org.chewing.v1.dto.response.chat.ChatRoomListResponse
import org.chewing.v1.facade.AiFacade
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.chat.AiChatRoomService
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.security.CurrentUser
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class AiController(
    private val aiFacade: AiFacade,
    private val aiChatRoomService: AiChatRoomService
) {
    @PostMapping("/ai/chat/room")
    fun createAiChatRoom(
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<ChatRoomId> {
        val chatRoomId = aiChatRoomService.createAiChatRoom(userId)
        return ResponseHelper.success(chatRoomId)
    }
}
