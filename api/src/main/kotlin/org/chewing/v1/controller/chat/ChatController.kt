package org.chewing.v1.controller.chat

import org.chewing.v1.dto.request.chat.ChatRequest
import org.chewing.v1.facade.DirectChatFacade
import org.chewing.v1.facade.GroupChatFacade
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.SuccessCreateResponse
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.helper.FileHelper
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.security.CurrentUser
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@Controller
class ChatController(
    private val directChatFacade: DirectChatFacade,
    private val groupChatFacade: GroupChatFacade,
) {
    @MessageMapping("/chat/direct/read")
    fun readDirectMessage(
        message: ChatRequest.Read,
        principal: Principal,
    ) {
        val userId = principal.name
        directChatFacade.processDirectChatRead(message.toChatRoomId(), UserId.of(userId), message.toSequenceNumber())
    }

    @MessageMapping("/chat/group/read")
    fun readGroupMessage(
        message: ChatRequest.Read,
        principal: Principal,
    ) {
        val userId = principal.name
        groupChatFacade.processGroupChatRead(message.toChatRoomId(), UserId.of(userId), message.toSequenceNumber())
    }

    @MessageMapping("/chat/direct/delete")
    fun deleteDirectMessage(
        message: ChatRequest.Delete,
        principal: Principal,
    ) {
        val userId = principal.name
        directChatFacade.processDirectChatDelete(message.toChatRoomId(), UserId.of(userId), message.messageId)
    }

    @MessageMapping("/chat/group/delete")
    fun deleteGroupMessage(
        message: ChatRequest.Delete,
        principal: Principal,
    ) {
        val userId = principal.name
        groupChatFacade.processGroupChatDelete(message.toChatRoomId(), UserId.of(userId), message.messageId)
    }

    @MessageMapping("/chat/direct/reply")
    fun replyDirectMessage(
        message: ChatRequest.Reply,
        principal: Principal,
    ) {
        val userId = principal.name
        directChatFacade.processDirectChatReply(message.toChatRoomId(), UserId.of(userId), message.parentMessageId, message.message)
    }

    @MessageMapping("/chat/group/reply")
    fun replyGroupMessage(
        message: ChatRequest.Reply,
        principal: Principal,
    ) {
        val userId = principal.name
        groupChatFacade.processGroupChatReply(message.toChatRoomId(), UserId.of(userId), message.parentMessageId, message.message)
    }

    @MessageMapping("/chat/direct/common")
    fun chatDirectMessage(
        message: ChatRequest.Common,
        principal: Principal,
    ) {
        val userId = principal.name
        directChatFacade.processDirectChatCommon(message.toChatRoomId(), UserId.of(userId), message.message)
    }

    @MessageMapping("/chat/group/common")
    fun chatGroupMessage(
        message: ChatRequest.Common,
        principal: Principal,
    ) {
        val userId = principal.name
        groupChatFacade.processGroupChatCommon(message.toChatRoomId(), UserId.of(userId), message.message)
    }

    @PostMapping("/api/chat/direct/file/upload")
    fun uploadDirectFiles(
        @RequestPart("files") files: List<MultipartFile>,
        @CurrentUser userId: UserId,
        @RequestParam("chatRoomId") chatRoomId: String,
    ): SuccessResponseEntity<SuccessCreateResponse> {
        val convertFiles = FileHelper.convertMultipartFileToFileDataList(files)
        directChatFacade.processDirectChatFiles(convertFiles, userId, ChatRoomId.of(chatRoomId))
        return ResponseHelper.successCreateOnly()
    }

    @PostMapping("/api/chat/group/file/upload")
    fun uploadGroupFiles(
        @RequestPart("files") files: List<MultipartFile>,
        @CurrentUser userId: UserId,
        @RequestParam("chatRoomId") chatRoomId: String,
    ): SuccessResponseEntity<SuccessCreateResponse> {
        val convertFiles = FileHelper.convertMultipartFileToFileDataList(files)
        groupChatFacade.processGroupChatFiles(convertFiles, userId, ChatRoomId.of(chatRoomId))
        return ResponseHelper.successCreateOnly()
    }
}
