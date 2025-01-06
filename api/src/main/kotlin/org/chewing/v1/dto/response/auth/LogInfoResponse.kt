package org.chewing.v1.dto.response.auth

import org.chewing.v1.model.auth.LoginInfo

data class LogInfoResponse(
    val token: TokenResponse,
    val access: String,
) {
    companion object {
        fun of(loginInfo: LoginInfo): LogInfoResponse {
            val tokenResponse = TokenResponse.of(loginInfo.jwtToken)
            return LogInfoResponse(tokenResponse, loginInfo.loginType.toString().lowercase())
        }
    }
}
