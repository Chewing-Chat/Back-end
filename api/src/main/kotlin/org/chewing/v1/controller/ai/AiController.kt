package org.chewing.v1.controller.ai

import org.chewing.v1.dto.request.chat.ChatRequest
import org.chewing.v1.dto.request.chat.ClonePromptRequest
import org.chewing.v1.dto.response.chat.AiChatMessageResponse
import org.chewing.v1.facade.AiFacade
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.chat.AiChatRoomService
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.security.CurrentUser
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class AiController(
    private val aiFacade: AiFacade,
    private val aiChatRoomService: AiChatRoomService,
) {
    @PostMapping("/ai/chat/room")
    fun createAiChatRoom(
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<ChatRoomId> {
        val chatRoomId = aiFacade.produceAiChatRoom(userId)
        return ResponseHelper.success(chatRoomId)
    }

    @PostMapping("/ai/chat/room/prompt")
    fun promptAiChatRoom(
        @CurrentUser userId: UserId,
        @RequestBody request: ChatRequest.Common,
    ): SuccessResponseEntity<AiChatMessageResponse> {
        val prompt = aiFacade.processAiMessage(userId, request.toChatRoomId(), request.toMessage())
        return ResponseHelper.success(AiChatMessageResponse.of(prompt))
    }

    @PostMapping("/ai/chat/clone/from-room/{chatRoomId}")
    fun cloneFromDirectChatRoom(
        @CurrentUser userId: UserId,
        @PathVariable chatRoomId: String,
        @RequestBody request: ClonePromptRequest,
    ): SuccessResponseEntity<AiChatMessageResponse> {
        val result = aiFacade.cloneChatAsUserFromChatRoom(
            requester = userId,
            chatRoomId = ChatRoomId.of(chatRoomId),
            prompt = request.prompt
        )
        return ResponseHelper.success(AiChatMessageResponse.of(result))
    }


}
