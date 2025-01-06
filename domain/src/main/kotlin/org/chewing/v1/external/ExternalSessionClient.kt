package org.chewing.v1.external
interface ExternalSessionClient {
    fun connect(userId: String, sessionId: String)
    fun isOnline(userId: String): Boolean
    fun getSessionId(userId: String): String
    fun unConnect(userId: String, sessionId: String)
}
