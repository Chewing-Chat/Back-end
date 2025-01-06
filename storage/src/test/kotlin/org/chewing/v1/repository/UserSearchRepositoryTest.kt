package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jpaentity.friend.UserSearchJpaEntity
import org.chewing.v1.jparepository.user.UserSearchJpaRepository
import org.chewing.v1.repository.jpa.user.UserSearchRepositoryImpl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class UserSearchRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var userSearchJpaRepository: UserSearchJpaRepository

    @Autowired
    private lateinit var userSearchRepositoryImpl: UserSearchRepositoryImpl

    @Test
    fun `검색 히스토리 추가`() {
        // given
        val userId = generateUserId()
        val keyword = "keyword"

        // when
        userSearchRepositoryImpl.appendHistory(userId, keyword)

        // then
        val searchHistory = userSearchJpaRepository.findAllByUserIdOrderByCreatedAt(userId)
        assert(searchHistory.size == 1)
    }

    @Test
    fun `검색 히스토리 조회`() {
        // given
        val userId = generateUserId()
        val keyword = "keyword"
        userSearchJpaRepository.save(UserSearchJpaEntity.fromFriendSearch(userId, keyword))

        // when
        val searchHistory = userSearchRepositoryImpl.readSearchHistory(userId)

        // then
        assert(searchHistory.size == 1)
    }

    fun generateUserId(): String = UUID.randomUUID().toString()
}
