package org.chewing.v1.implementation.user.user

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.media.Media
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Component

/**
 * UserUpdater는 사용자 정보를 업데이트하는 구현체입니다.
 * 데이터베이스에서 사용자 정보를 업데이트하고, 캐시에서 해당 사용자의 정보를 제거합니다.
 */
@Component
class UserUpdater(
    private val userRepository: UserRepository,
) {
    /**
     * 주어진 사용자 정보를 업데이트합니다.
     */
    fun updateFileUrl(userId: String, media: Media): Media = userRepository.updateMedia(userId, media) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)

    fun updatePassword(userId: String, password: String) {
        userRepository.updatePassword(userId, password) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
    }
}
