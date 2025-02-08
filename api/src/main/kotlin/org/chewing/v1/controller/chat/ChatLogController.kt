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

    @GetMapping("/direct/log")
    fun getDirectChatLog(@PathVariable chatRoomId: String, @RequestParam sequenceNumber: Int): HttpResponse<ChatLogResponse> {
        val chatLog = chatLogService.getChatLog(chatRoomId, sequenceNumber)
        return HttpResponse.success(ChatLogResponse.from(chatLog))
    }

    @GetMapping("/group/log")
    fun getGroupChatLog(@PathVariable chatRoomId: String, @RequestParam sequenceNumber: Int): HttpResponse<ChatLogResponse> {
        val chatLog = chatLogService.getGroupChatLog(chatRoomId, sequenceNumber)
        return HttpResponse.success(ChatLogResponse.from(chatLog))
    }
}
