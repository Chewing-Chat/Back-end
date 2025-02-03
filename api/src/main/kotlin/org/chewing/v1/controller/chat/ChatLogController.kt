package org.chewing.v1.controller.chat

import org.chewing.v1.service.chat.ChatLogService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chatRooms/{chatRoomId}")
class ChatLogController(
    private val chatLogService: ChatLogService,
) {
//
//    @GetMapping("/log")
//    fun getChatLog(@PathVariable chatRoomId: String, @RequestParam page: Int): HttpResponse<ChatLogResponse> {
//        val chatLog = chatLogService.getChatLog(chatRoomId, page)
//        return HttpResponse.success(ChatLogResponse.from(chatLog))
//    }
}
