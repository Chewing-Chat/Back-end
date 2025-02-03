package org.chewing.v1.external

import org.chewing.v1.client.SessionClient
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class ExternalSessionClientImpl(
    private val sessionClient: SessionClient,
) : ExternalSessionClient {
    override fun connect(userId: UserId, sessionId: String) {
        sessionClient.addSession(userId, sessionId)
    }
    override fun isOnline(userId: UserId): Boolean = sessionClient.isUserOnline(userId)

    override fun getSessionId(userId: UserId): String = sessionClient.getSessionId(userId)

    override fun unConnect(userId: UserId, sessionId: String) {
        sessionClient.removeSession(userId, sessionId)
    }
}
