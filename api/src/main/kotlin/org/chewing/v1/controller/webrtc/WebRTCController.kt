package org.chewing.v1.controller.web

import org.chewing.v1.model.webrtc.IceCandidateMessage
import org.chewing.v1.model.webrtc.WebRTCMessage
import org.chewing.v1.service.web.WebRTCSessionManager
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class WebRTCController(private val sessionManager: WebRTCSessionManager) {

    @MessageMapping("/call")
    @SendTo("/topic/call")
    fun initiateCall(message: WebRTCMessage): WebRTCMessage {
        val session = sessionManager.getSession(message.userId)
        return if (session != null) {
            message
        } else {
            throw IllegalStateException("세션이 없습니다.")
        }
    }

    @MessageMapping("/ice")
    @SendTo("/topic/ice")
    fun exchangeIceCandidates(iceMessage: IceCandidateMessage): IceCandidateMessage {
        return iceMessage
    }

    @MessageMapping("/endCall")
    @SendTo("/topic/endCall")
    fun endCall(userId: String): String {
        sessionManager.removeSession(userId)
        return "Call Ended"
    }
}
