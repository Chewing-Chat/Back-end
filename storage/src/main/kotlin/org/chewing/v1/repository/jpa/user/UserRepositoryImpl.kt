package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.user.UserJpaEntity
import org.chewing.v1.jparepository.user.UserJpaRepository
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
internal class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun read(userId: UserId): User? {
        val userEntity = userJpaRepository.findById(userId.id)
        return userEntity.map { it.toUser() }.orElse(null)
    }

    override fun reads(userIds: List<UserId>): List<User> {
        val userEntities = userJpaRepository.findAllById(userIds.map { it.id })
        return userEntities.map { it.toUser() }
    }

    override fun append(credential: Credential, userName: String): User = when (credential) {
        is PhoneNumber -> {
            val userEntity = UserJpaEntity.generate(credential, userName, AccessStatus.NEED_CREATE_PASSWORD)
            userJpaRepository.save(userEntity).toUser()
        }
    }

    override fun remove(userId: UserId): User? = userJpaRepository.findById(userId.id)
        .map { entity ->
            entity.updateAccessStatus(AccessStatus.DELETE)
            userJpaRepository.save(entity)
            entity.toUser()
        }.orElse(null)

    override fun updateMedia(userId: UserId, media: Media): Media? = userJpaRepository.findById(userId.id).map { user ->
        // 수정 전 기존 미디어 정보를 반환
        val previousMedia = user.toUser().image

        // 새로운 미디어 정보 업데이트
        user.updateUserPictureUrl(media)

        // 사용자 정보 저장
        userJpaRepository.save(user)

        // 수정 전 정보를 반환
        previousMedia
    }.orElse(null)

    override fun updatePassword(userId: UserId, password: String): UserId? = userJpaRepository.findById(userId.id).map {
        it.updatePassword(password)
        userJpaRepository.save(it)
        it.toUserId()
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

    override fun updateStatusMessage(userId: UserId, statusMessage: String): UserId? {
        return userJpaRepository.findById(userId.id).map {
            it.updateStatusMessage(statusMessage)
            userJpaRepository.save(it)
            it.toUserId()
        }.orElse(null)
    }
}
