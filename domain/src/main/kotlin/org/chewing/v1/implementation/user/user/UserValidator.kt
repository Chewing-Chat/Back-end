package org.chewing.v1.implementation.user.user

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.springframework.stereotype.Component

@Component
class UserValidator() {
    fun isUserAccess(user: User) {
        if (user.status != AccessStatus.ACCESS) {
            throw ConflictException(ErrorCode.USER_NOT_ACCESS)
        }
    }
}
