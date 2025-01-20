package org.chewing.v1.implementation.user.user

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.PushNotificationRepository
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Component

/**
 * UserReader는 사용자 정보를 읽어오는 구현체입니다.
 * 데이터베이스와 캐시 저장소에서 사용자 정보를 읽어오고,
 * 사용자 정보를 캐시에 추가하여 재사용성을 높입니다.
 */
@Component
class UserReader(
    private val userRepository: UserRepository,
    private val pushNotificationRepository: PushNotificationRepository,
) {
    /**
     * 주어진 사용자 ID에 해당하는 사용자 정보를 읽어옵니다.
     * @throws NotFoundException 사용자가 존재하지 않는 경우,
     * USER_NOT_FOUND 오류 코드와 함께 예외를 발생시킵니다.
     */
    fun read(userId: UserId): User {
        return userRepository.read(userId) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
    }

    fun readByCredential(credential: Credential, accessStatus: AccessStatus): User {
        return userRepository.readByCredential(credential, accessStatus) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
    }

    fun reads(userIds: List<UserId>): List<User> {
        return userRepository.reads(userIds)
    }

    fun readsPushToken(userId: UserId): List<PushToken> {
        return pushNotificationRepository.reads(userId)
    }
}
