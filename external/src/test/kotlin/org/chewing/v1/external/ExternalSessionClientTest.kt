package org.chewing.v1.external

import org.chewing.v1.client.SessionClient
import org.junit.jupiter.api.Test

class ExternalSessionClientTest {
    private val sessionClient = SessionClient()
    private val externalSessionClient = ExternalSessionClientImpl(sessionClient)

    @Test
    fun testConnect() {
        externalSessionClient.connect("userId", "sessionId")
        assert(sessionClient.isUserOnline("userId"))
    }

    @Test
    fun testIsOnline() {
        sessionClient.addSession("userId", "sessionId")
        assert(externalSessionClient.isOnline("userId"))
    }

    @Test
    fun testGetSessionId() {
        sessionClient.addSession("userId", "sessionId")
        assert(externalSessionClient.getSessionId("userId") == "sessionId")
    }

    @Test
    fun testUnConnect() {
        sessionClient.addSession("userId", "sessionId")
        externalSessionClient.unConnect("userId", "sessionId")
        assert(!sessionClient.isUserOnline("userId"))
    }
}
