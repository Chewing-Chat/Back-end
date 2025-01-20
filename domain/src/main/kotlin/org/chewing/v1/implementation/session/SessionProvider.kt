package org.chewing.v1.implementation.session

import org.chewing.v1.external.ExternalSessionClient
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class SessionProvider(
    private val externalSessionClient: ExternalSessionClient,

) {
    fun connect(userId: UserId, sessionId: String) {
        externalSessionClient.connect(userId, sessionId)
    }

    fun isOnline(userId: UserId): Boolean {
        return externalSessionClient.isOnline(userId)
    }

    fun unConnect(userId: UserId, sessionId: String) {
        externalSessionClient.unConnect(userId, sessionId)
    }
}
