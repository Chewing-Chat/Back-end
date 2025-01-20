package org.chewing.v1.model.webrtc

import org.chewing.v1.model.user.UserId

data class WebRTCMessage(
    val userId: String,
    val type: String,
    val sdp: String? = null,
)
