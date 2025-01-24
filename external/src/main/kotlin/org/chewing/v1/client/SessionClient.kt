package org.chewing.v1.client

import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class SessionClient {

    private val sessions = ConcurrentHashMap<String, MutableSet<String>>()

    fun addSession(userId: UserId, sessionId: String) {
        sessions.computeIfAbsent(userId.id) { ConcurrentHashMap.newKeySet() }.add(sessionId)
    }

    fun removeSession(userId: UserId, sessionId: String) {
        sessions[userId.id]?.remove(sessionId)
        if (sessions[userId.id]?.isEmpty() == true) {
            sessions.remove(userId.id)
        }
    }

    fun isUserOnline(userId: UserId): Boolean = sessions.containsKey(userId.id)

    fun getSessionId(userId: UserId): String = sessions[userId.id]?.firstOrNull() ?: ""
}
