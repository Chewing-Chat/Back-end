package org.chewing.v1.model.webrtc

data class IceCandidateMessage(
    val candidate: String,
    val sdpMid: String,
    val sdpMLineIndex: Int,
)
