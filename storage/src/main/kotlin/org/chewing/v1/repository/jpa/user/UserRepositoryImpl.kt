package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.user.UserJpaEntity
import org.chewing.v1.jparepository.user.UserJpaRepository
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
internal class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun read(userId: String): User? {
        val userEntity = userJpaRepository.findById(userId)
        return userEntity.map { it.toUser() }.orElse(null)
    }

    override fun reads(userIds: List<String>): List<User> {
        val userEntities = userJpaRepository.findAllById(userIds.map { it })
        return userEntities.map { it.toUser() }
    }

    override fun append(credential: Credential, userName: String): User = when (credential) {
        is PhoneNumber -> {
            val userEntity = UserJpaEntity.generate(credential, userName, AccessStatus.NEED_CREATE_PASSWORD)
            userJpaRepository.save(userEntity).toUser()
        }
    }

    override fun remove(userId: String): User? = userJpaRepository.findById(userId)
        .map { entity ->
            entity.updateAccessStatus(AccessStatus.DELETE)
            userJpaRepository.save(entity)
            entity.toUser()
        }.orElse(null)

    override fun updateMedia(userId: String, media: Media): Media? = userJpaRepository.findById(userId).map { user ->
        // 수정 전 기존 미디어 정보를 반환
        val previousMedia = user.toUser().image

        // 새로운 미디어 정보 업데이트
        user.updateUserPictureUrl(media)

        // 사용자 정보 저장
        userJpaRepository.save(user)

        // 수정 전 정보를 반환
        previousMedia
    }.orElse(null)

    override fun updatePassword(userId: String, password: String): String? = userJpaRepository.findById(userId).map {
        it.updatePassword(password)
        userJpaRepository.save(it)
        userId
    }.orElse(null)

    override fun readByCredential(credential: Credential, accessStatus: AccessStatus): User? = when (credential) {
        is PhoneNumber -> userJpaRepository.findUserJpaEntityByCountryCodeAndPhoneNumberAndType(
            credential.countryCode,
            credential.number,
            accessStatus,
        ).map {
            it.toUser()
        }.orElse(null)
    }

    override fun updateStatusMessage(userId: String, statusMessage: String): String? {
        return userJpaRepository.findById(userId).map {
            it.updateStatusMessage(statusMessage)
            userJpaRepository.save(it)
            userId
        }.orElse(null)
    }
}
