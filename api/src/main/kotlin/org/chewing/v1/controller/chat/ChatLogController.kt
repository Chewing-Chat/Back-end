package org.chewing.v1.controller.chat

import org.chewing.v1.dto.response.chat.ChatLogsResponse
import org.chewing.v1.facade.DirectChatFacade
import org.chewing.v1.facade.GroupChatFacade
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.HttpResponse
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chatRoom/{chatRoomId}")
class ChatLogController(
    private val groupChatFacade: GroupChatFacade,
    private val directChatFacade: DirectChatFacade,
) {

    @GetMapping("/direct/log")
    fun getDirectChatLog(
        @CurrentUser userId: UserId,
        @PathVariable chatRoomId: String,
        @RequestParam sequenceNumber: Int,
    ): SuccessResponseEntity<ChatLogsResponse> {
        val chatLog = directChatFacade.processDirectChatLogs(userId, ChatRoomId.of(chatRoomId), sequenceNumber)
        return ResponseHelper.success(ChatLogsResponse.from(chatLog))
    }

    @GetMapping("/group/log")
    fun getGroupChatLog(
        @CurrentUser userId: UserId,
        @PathVariable chatRoomId: String,
        @RequestParam sequenceNumber: Int,
    ): SuccessResponseEntity<ChatLogsResponse> {
        val chatLog = groupChatFacade.processGroupChatLogs(userId, ChatRoomId.of(chatRoomId), sequenceNumber)
        return ResponseHelper.success(ChatLogsResponse.from(chatLog))
    }
}
