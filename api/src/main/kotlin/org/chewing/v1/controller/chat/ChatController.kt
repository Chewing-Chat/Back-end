package org.chewing.v1.controller.chat

import org.chewing.v1.dto.request.chat.ChatRequest
import org.chewing.v1.facade.ChatFacade
import org.chewing.v1.response.SuccessCreateResponse
import org.chewing.v1.util.helper.FileHelper
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@Controller
class ChatController(
    private val chatFacade: ChatFacade,
) {
    @MessageMapping("/chat/read")
    fun readMessage(
        message: ChatRequest.Read,
        principal: Principal,
    ) {
        val userId = principal.name
        chatFacade.processRead(message.chatRoomId, userId)
    }

    @MessageMapping("/chat/delete")
    fun deleteMessage(
        message: ChatRequest.Delete,
        principal: Principal,
    ) {
        val userId = principal.name
        chatFacade.processDelete(message.chatRoomId, userId, message.messageId)
    }

    @MessageMapping("/chat/reply")
    fun replyMessage(
        message: ChatRequest.Reply,
        principal: Principal,
    ) {
        val userId = principal.name
        chatFacade.processReply(message.chatRoomId, userId, message.parentMessageId, message.message)
    }

    @MessageMapping("/chat/bomb")
    fun bombMessage(
        message: ChatRequest.Bomb,
        principal: Principal,
    ) {
        val userId = principal.name
        chatFacade.processBombing(message.chatRoomId, userId, message.message, message.toExpireAt())
    }

    @MessageMapping("/chat/common")
    fun chatMessage(
        message: ChatRequest.Common,
        principal: Principal,
    ) {
        val userId = principal.name
        chatFacade.processCommon(message.chatRoomId, userId, message.message)
    }

    @PostMapping("/api/chat/file/upload")
    fun uploadFiles(
        @RequestPart("files") files: List<MultipartFile>,
        @RequestAttribute("userId") userId: String,
        @RequestParam("chatRoomId") chatRoomId: String,
    ): SuccessResponseEntity<SuccessCreateResponse> {
        val convertFiles = FileHelper.convertMultipartFileToFileDataList(files)
        chatFacade.processFiles(convertFiles, userId, chatRoomId)
        // 생성 완료 응답 201 반환
        return ResponseHelper.successCreateOnly()
    }
}
