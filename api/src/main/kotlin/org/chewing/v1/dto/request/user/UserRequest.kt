package org.chewing.v1.dto.request.user

class UserRequest {
    data class UpdateStatusMessage(
        val statusMessage: String
    ) {
        fun toStatusMessage(): String = statusMessage
    }
}
