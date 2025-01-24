package org.chewing.v1.controller.chat

import org.chewing.v1.dto.response.chat.ChatLogResponse
import org.chewing.v1.response.HttpResponse
import org.chewing.v1.service.chat.ChatLogService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chatRooms/{chatRoomId}")
class ChatLogController(
    private val chatLogService: ChatLogService,
) {

    @GetMapping("/log")
    fun getChatLog(@PathVariable chatRoomId: String, @RequestParam page: Int): HttpResponse<ChatLogResponse> {
        val chatLog = chatLogService.getChatLog(chatRoomId, page)
        return HttpResponse.success(ChatLogResponse.from(chatLog))
    }
}
