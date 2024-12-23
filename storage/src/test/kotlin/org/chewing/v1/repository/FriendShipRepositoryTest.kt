package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jpaentity.friend.FriendShipId
import org.chewing.v1.jparepository.friend.FriendShipJpaRepository
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.repository.jpa.friend.FriendShipRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.repository.support.UserProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class FriendShipRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var friendShipJpaRepository: FriendShipJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    private val friendShipRepositoryImpl: FriendShipRepositoryImpl by lazy {
        FriendShipRepositoryImpl(friendShipJpaRepository)
    }

    @Test
    fun `친구 관계를 저장한다 상대 친구를 내가 원하는 이름으로 저장한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val friendName = UserProvider.buildFriendName()

        // when
        friendShipRepositoryImpl.append(userId, friendId, friendName)

        // then
        val friendShipTarget = friendShipJpaRepository.findById(FriendShipId(userId, friendId)).get().toFriendShip()
        assert(friendShipTarget.friendName.firstName == friendName.firstName())
        assert(friendShipTarget.friendName.lastName == friendName.lastName())
        assert(friendShipTarget.friendId == friendId)
        assert(!friendShipTarget.isFavorite)
    }

    @Test
    fun `친구 관계를 삭제한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, AccessStatus.ACCESS)
        friendShipRepositoryImpl.remove(userId, friendId)

        val entity = friendShipJpaRepository.findById(FriendShipId(userId, friendId))
        assert(entity.get().toFriendShip().type == AccessStatus.DELETE)
    }

    @Test
    fun `친구 관계를 차단한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        jpaDataGenerator.friendShipEntityData(userId, friendId, AccessStatus.ACCESS)
        friendShipRepositoryImpl.block(userId, friendId)

        val entity = friendShipJpaRepository.findById(FriendShipId(userId, friendId))

        assert(entity.get().toFriendShip().type == AccessStatus.BLOCK)
    }

    @Test
    fun `친구 관계를 차단당한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, AccessStatus.ACCESS)
        friendShipRepositoryImpl.blocked(userId, friendId)

        val entity = friendShipJpaRepository.findById(FriendShipId(userId, friendId))

        assert(entity.get().toFriendShip().type == AccessStatus.BLOCKED)
    }

    @Test
    fun `친구 관계를 조회한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, AccessStatus.ACCESS)
        val friendShip = friendShipRepositoryImpl.read(userId, friendId)

        assert(friendShip != null)
        assert(friendShip!!.friendId == friendId)
    }

    @Test
    fun `친구 관계를 즐겨찾기 설정한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, AccessStatus.ACCESS)
        friendShipRepositoryImpl.updateFavorite(userId, friendId, true)

        val entity = friendShipJpaRepository.findById(FriendShipId(userId, friendId))

        assert(entity.get().toFriendShip().isFavorite)
    }

    @Test
    fun `친구 관계를 이름을 변경한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val newName = UserProvider.buildNewUserName()

        jpaDataGenerator.friendShipEntityData(userId, friendId, AccessStatus.ACCESS)
        friendShipRepositoryImpl.updateName(userId, friendId, newName)

        val entity = friendShipJpaRepository.findById(FriendShipId(userId, friendId))

        assert(entity.get().toFriendShip().friendName.firstName == newName.firstName())
        assert(entity.get().toFriendShip().friendName.lastName == newName.lastName())
    }

    @Test
    fun `친구 관계를 접근 상태에 따라 조회한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val friendId2 = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, AccessStatus.ACCESS)
        jpaDataGenerator.friendShipEntityData(userId, friendId2, AccessStatus.BLOCK)

        val friendShips = friendShipRepositoryImpl.readsAccess(userId, AccessStatus.ACCESS, FriendSortCriteria.NAME)

        assert(friendShips.isNotEmpty())
        assert(friendShips.size == 1)
    }

    @Test
    fun `친구 관계를 접근 상태에 따라 여러개 조회한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val friendId2 = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, AccessStatus.ACCESS)
        jpaDataGenerator.friendShipEntityData(userId, friendId2, AccessStatus.BLOCKED)

        val friendShips = friendShipRepositoryImpl.reads(listOf(friendId, friendId2), userId, AccessStatus.ACCESS)

        assert(friendShips.isNotEmpty())
        assert(friendShips.size == 1)
    }

    private fun generateUserId() = UUID.randomUUID().toString()
}
