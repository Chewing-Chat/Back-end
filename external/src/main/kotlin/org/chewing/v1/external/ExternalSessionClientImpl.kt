package org.chewing.v1.external

import org.chewing.v1.client.SessionClient
import org.springframework.stereotype.Component

@Component
class ExternalSessionClientImpl(
    private val sessionClient: SessionClient,
) : ExternalSessionClient {
    override fun connect(userId: String, sessionId: String) {
        sessionClient.addSession(userId, sessionId)
    }
    override fun isOnline(userId: String): Boolean = sessionClient.isUserOnline(userId)

    override fun getSessionId(userId: String): String = sessionClient.getSessionId(userId)

    override fun unConnect(userId: String, sessionId: String) {
        sessionClient.removeSession(userId, sessionId)
    }
}
