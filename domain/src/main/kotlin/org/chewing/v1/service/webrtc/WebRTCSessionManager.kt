package org.chewing.v1.service.web

import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class WebRTCSessionManager {
    private val sessions = ConcurrentHashMap<String, String>() // 사용자 ID를 키로 사용

    fun addSession(userId: String, sessionId: String) {
        sessions[userId] = sessionId
    }

    fun removeSession(userId: String) {
        sessions.remove(userId)
    }

    fun getSession(userId: String): String? {
        return sessions[userId]
    }
}
