package org.chewing.v1.model.webrtc

data class WebRTCMessage(
    val userId: String,
    val type: String,
    val sdp: String? = null,
)
