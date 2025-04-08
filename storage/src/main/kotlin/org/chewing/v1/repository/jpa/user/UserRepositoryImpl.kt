package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.user.UserJpaEntity
import org.chewing.v1.jparepository.user.UserJpaRepository
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.contact.PhoneNumber
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
internal class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun read(userId: UserId, status: AccessStatus): UserInfo? {
        val userEntity = userJpaRepository.findByUserIdAndStatus(userId.id, status)
        return userEntity.map { it.toUser() }.orElse(null)
    }

    override fun reads(userIds: List<UserId>, status: AccessStatus): List<UserInfo> {
        val userEntities = userJpaRepository.findAllByUserIdInAndStatus(userIds.map { it.id }, status)
        return userEntities.map { it.toUser() }
    }

    override fun append(contact: Contact, userName: String): UserInfo = when (contact) {
        is PhoneNumber -> {
            userJpaRepository.findUserJpaEntityByPhoneNumberAndStatus(contact.e164PhoneNumber, AccessStatus.NEED_CREATE_PASSWORD)
                .map {
                    it.updateUserName(userName)
                    userJpaRepository.save(it)
                    it.toUser()
                }
                .orElseGet {
                    val userEntity = UserJpaEntity.generate(contact, userName, AccessStatus.NEED_CREATE_PASSWORD)
                    userJpaRepository.save(userEntity).toUser()
                }
        }
    }

    override fun remove(userId: UserId): UserInfo? = userJpaRepository.findById(userId.id)
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

    override fun readByContact(contact: Contact, accessStatus: AccessStatus): UserInfo? = when (contact) {
        is PhoneNumber -> userJpaRepository.findUserJpaEntityByPhoneNumberAndStatus(
            contact.e164PhoneNumber,
            accessStatus,
        ).map {
            it.toUser()
        }.orElse(null)
    }

    override fun readsByContacts(contacts: List<Contact>, accessStatus: AccessStatus): List<UserInfo> {
        val phoneNumbers = contacts.mapNotNull { it as? PhoneNumber }.map { it.e164PhoneNumber }
        val userEntities = userJpaRepository.findUserJpaEntitiesByPhoneNumberInAndStatus(phoneNumbers, accessStatus)
        return userEntities.map { it.toUser() }
    }

    override fun updateStatusMessage(userId: UserId, statusMessage: String): UserId? {
        return userJpaRepository.findById(userId.id).map {
            it.updateStatusMessage(statusMessage)
            userJpaRepository.save(it)
            it.toUserId()
        }.orElse(null)
    }

    override fun updateBirthday(
        userId: UserId,
        birthday: LocalDate,
    ): UserId? {
        return userJpaRepository.findById(userId.id).map {
            it.updateBirthday(birthday)
            userJpaRepository.save(it)
            it.toUserId()
        }.orElse(null)
    }

    override fun appendPassword(userId: UserId, password: String) {
        userJpaRepository.findById(userId.id).map {
            it.updateAccessStatus(AccessStatus.ACCESS)
            it.updatePassword(password)
            userJpaRepository.save(it)
        }
    }
}
