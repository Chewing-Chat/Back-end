package org.chewing.v1.implementation.session

import org.chewing.v1.external.ExternalSessionClient
import org.springframework.stereotype.Component

@Component
class SessionProvider(
    private val externalSessionClient: ExternalSessionClient,

) {
    fun connect(userId: String, sessionId: String) {
        externalSessionClient.connect(userId, sessionId)
    }

    fun isOnline(userId: String): Boolean {
        return externalSessionClient.isOnline(userId)
    }

    fun unConnect(userId: String, sessionId: String) {
        externalSessionClient.unConnect(userId, sessionId)
    }
}
