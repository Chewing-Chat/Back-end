package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jpaentity.friend.FriendShipId
import org.chewing.v1.jparepository.friend.FriendShipJpaRepository
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.user.UserId
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
        friendShipRepositoryImpl.appendIfNotExist(userId, friendId, friendName, FriendShipStatus.FRIEND)

        // then
        val friendShipTarget = friendShipJpaRepository.findById(FriendShipId.of(userId, friendId)).get().toFriendShip()
        assert(friendShipTarget.friendName == friendName)
        assert(friendShipTarget.friendId == friendId)
        assert(!friendShipTarget.isFavorite)
    }

    @Test
    fun `친구 관계를 삭제한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, FriendShipStatus.FRIEND)
        friendShipRepositoryImpl.remove(userId, friendId)

        val entity = friendShipJpaRepository.findById(FriendShipId.of(userId, friendId))
        assert(entity.get().toFriendShip().status == FriendShipStatus.DELETE)
    }

    @Test
    fun `친구 관계를 차단한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        jpaDataGenerator.friendShipEntityData(userId, friendId, FriendShipStatus.FRIEND)
        friendShipRepositoryImpl.block(userId, friendId)

        val entity = friendShipJpaRepository.findById(FriendShipId.of(userId, friendId))

        assert(entity.get().toFriendShip().status == FriendShipStatus.BLOCK)
    }

    @Test
    fun `친구 관계를 차단당한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, FriendShipStatus.FRIEND)
        friendShipRepositoryImpl.blocked(userId, friendId)

        val entity = friendShipJpaRepository.findById(FriendShipId.of(userId, friendId))

        assert(entity.get().toFriendShip().status == FriendShipStatus.BLOCKED)
    }

    @Test
    fun `친구 관계를 조회한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, FriendShipStatus.FRIEND)
        val friendShip = friendShipRepositoryImpl.readByRelation(userId, friendId)

        assert(friendShip != null)
        assert(friendShip!!.friendId == friendId)
    }

    @Test
    fun `친구 관계를 즐겨찾기 설정한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, FriendShipStatus.FRIEND)
        friendShipRepositoryImpl.updateFavorite(userId, friendId, true)

        val entity = friendShipJpaRepository.findById(FriendShipId.of(userId, friendId))

        assert(entity.get().toFriendShip().isFavorite)
    }

    @Test
    fun `친구 관계를 즐겨찾기 취소 설정한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, FriendShipStatus.FRIEND)
        friendShipRepositoryImpl.updateFavorite(userId, friendId, true)
        friendShipRepositoryImpl.updateFavorite(userId, friendId, false)
        val entity = friendShipJpaRepository.findById(FriendShipId.of(userId, friendId))

        assert(entity.get().toFriendShip().isFavorite == false)
    }

    @Test
    fun `친구 관계를 이름을 변경한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val newName = UserProvider.buildNewUserName()

        jpaDataGenerator.friendShipEntityData(userId, friendId, FriendShipStatus.FRIEND)
        friendShipRepositoryImpl.updateName(userId, friendId, newName)

        val entity = friendShipJpaRepository.findById(FriendShipId.of(userId, friendId))

        assert(entity.get().toFriendShip().friendName == newName)
    }

    @Test
    fun `친구 관계 목록을 조회한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val friendId2 = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, FriendShipStatus.FRIEND)
        jpaDataGenerator.friendShipEntityData(userId, friendId2, FriendShipStatus.BLOCK)

        val friendShips = friendShipRepositoryImpl.reads(userId)

        assert(friendShips.isNotEmpty())
        assert(friendShips.size == 2)
    }

    @Test
    fun `즐겨찾기인 친구들을 검색한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val friendId2 = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, FriendShipStatus.FRIEND)
        jpaDataGenerator.friendShipEntityData(userId, friendId2, FriendShipStatus.FRIEND)
        friendShipRepositoryImpl.updateFavorite(userId, friendId, true)

        val friendShips = friendShipRepositoryImpl.readsFavorite(userId)

        assert(friendShips.isNotEmpty())
        assert(friendShips.size == 1)
    }

    @Test
    fun `친구 관계를 허용한다`() {
        val userId = generateUserId()
        val friendId = generateUserId()

        jpaDataGenerator.friendShipEntityData(userId, friendId, FriendShipStatus.BLOCK)
        friendShipRepositoryImpl.allowedFriend(userId, friendId, UserProvider.buildFriendName())

        val entity = friendShipJpaRepository.findById(FriendShipId.of(userId, friendId))

        assert(entity.get().toFriendShip().status == FriendShipStatus.FRIEND)
    }

    private fun generateUserId() = UserId.of(UUID.randomUUID().toString())
}
