package org.chewing.v1.model.auth

import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User

class LoginInfo private constructor(
    val jwtToken: JwtToken,
    val loginType: AccessStatus,
) {
    companion object {
        fun of(jwtToken: JwtToken, user: User): LoginInfo {
            return LoginInfo(jwtToken, user.status)
        }
    }
}
// 로그인 정보역시 jwt토큰과 user에서 받아옴
