package org.chewing.v1.controller.openvidu

import io.openvidu.java.client.OpenVidu
import io.openvidu.java.client.Session
import io.openvidu.java.client.TokenOptions
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class OpenViduController(private val openVidu: OpenVidu) {

    @GetMapping("/api/openvidu/session")
    fun createSession(): String {
        val session: Session = openVidu.createSession()
        return session.sessionId
    }

    @GetMapping("/api/openvidu/token")
    fun createToken(@RequestParam sessionId: String): String {
        val session = openVidu.getActiveSessions().find { it.sessionId == sessionId }
            ?: throw IllegalArgumentException("Session not found")
        return session.generateToken(TokenOptions.Builder().build())
    }
}
