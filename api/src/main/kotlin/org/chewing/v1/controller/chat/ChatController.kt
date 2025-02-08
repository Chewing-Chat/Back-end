package org.chewing.v1.controller.chat

import org.chewing.v1.facade.DirectChatFacade
import org.springframework.stereotype.Controller

@Controller
class ChatController(
    private val directChatFacade: DirectChatFacade,
) {
//    @MessageMapping("/chat/read")
//    fun readMessage(
//        message: ChatRequest.Read,
//        principal: Principal,
//    ) {
//        val userId = principal.name
//        chatFacade.processRead(message.chatRoomId, UserId.of(userId))
//    }
//
//    @MessageMapping("/chat/delete")
//    fun deleteMessage(
//        message: ChatRequest.Delete,
//        principal: Principal,
//    ) {
//        val userId = principal.name
//        chatFacade.processDelete(message.chatRoomId, UserId.of(userId), message.messageId)
//    }
//
//    @MessageMapping("/chat/reply")
//    fun replyMessage(
//        message: ChatRequest.Reply,
//        principal: Principal,
//    ) {
//        val userId = principal.name
//        chatFacade.processReply(message.chatRoomId, UserId.of(userId), message.parentMessageId, message.message)
//    }
//
//    @MessageMapping("/chat/common")
//    fun chatMessage(
//        message: ChatRequest.Common,
//        principal: Principal,
//    ) {
//        val userId = principal.name
//        chatFacade.processCommon(message.chatRoomId, UserId.of(userId), message.message)
//    }
//
//    @PostMapping("/api/chat/file/upload")
//    fun uploadFiles(
//        @RequestPart("files") files: List<MultipartFile>,
//        @RequestAttribute("userId") userId: String,
//        @RequestParam("chatRoomId") chatRoomId: String,
//    ): SuccessResponseEntity<SuccessCreateResponse> {
//        val convertFiles = FileHelper.convertMultipartFileToFileDataList(files)
//        chatFacade.processFiles(convertFiles, UserId.of(userId), chatRoomId)
//        // 생성 완료 응답 201 반환
//        return ResponseHelper.successCreateOnly()
//    }
}
