package org.chewing.v1.response

import org.chewing.v1.error.ErrorCode

data class ErrorResponse(
    val errorCode: String,
    val message: String,
) {
    companion object {
        fun from(errorCode: ErrorCode): ErrorResponse {
            return ErrorResponse(
                errorCode.code,
                errorCode.message,
            )
        }
    }
}
