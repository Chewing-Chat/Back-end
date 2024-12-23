package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.user.UserStatusJpaEntity
import org.chewing.v1.jparepository.user.UserStatusJpaRepository
import org.chewing.v1.model.user.UserStatus
import org.chewing.v1.repository.user.UserStatusRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class UserStatusRepositoryImpl(
    private val userStatusJpaRepository: UserStatusJpaRepository,
) : UserStatusRepository {
    @Transactional
    override fun removes(statusesId: List<String>) {
        userStatusJpaRepository.deleteAllByStatusIdIn(statusesId)
    }

    override fun readSelected(userId: String): UserStatus = userStatusJpaRepository.findBySelectedTrueAndUserId(userId).map {
        it.toUserStatus()
    }.orElse(UserStatus.default(userId))

    override fun readSelectedUsers(userIds: List<String>): List<UserStatus> = userStatusJpaRepository.findAllBySelectedTrueAndUserIdIn(userIds).map { it.toUserStatus() }

    @Transactional
    override fun removeAllByUserId(userId: String) {
        userStatusJpaRepository.deleteAllByUserId(userId)
    }

    override fun updateSelectedStatusTrue(userId: String, statusId: String) {
        userStatusJpaRepository.findById(statusId).ifPresent {
            it.updateSelectedTrue()
            userStatusJpaRepository.save(it)
        }
    }

    override fun updateSelectedStatusFalse(userId: String) {
        userStatusJpaRepository.findBySelectedTrueAndUserId(userId).ifPresent {
            it.updateSelectedFalse()
            userStatusJpaRepository.save(it)
        }
    }
    override fun append(userId: String, statusMessage: String, emoji: String) {
        userStatusJpaRepository.save(UserStatusJpaEntity.generate(userId, statusMessage, emoji))
    }
    override fun reads(userId: String): List<UserStatus> = userStatusJpaRepository.findAllByUserId(userId).map { it.toUserStatus() }
}
