package org.chewing.v1.dto.request.user

class UserStatusRequest {
    data class Update(
        val statusId: String,
    )
    data class Add(
        val emoji: String,
        val message: String,
    )
    data class Delete(
        val statusId: String,
    )
}
