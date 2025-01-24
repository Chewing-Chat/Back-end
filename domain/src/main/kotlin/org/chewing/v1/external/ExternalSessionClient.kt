package org.chewing.v1.external

import org.chewing.v1.model.user.UserId

interface ExternalSessionClient {
    fun connect(userId: UserId, sessionId: String)
    fun isOnline(userId: UserId): Boolean
    fun getSessionId(userId: UserId): String
    fun unConnect(userId: UserId, sessionId: String)
}
