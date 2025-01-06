package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.user.UserEmoticonJpaRepository
import org.chewing.v1.repository.jpa.user.UserEmoticonRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class UserEmoticonPackInfoTest : JpaContextTest() {

    @Autowired
    private lateinit var userEmoticonJpaRepository: UserEmoticonJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var userEmoticonRepositoryImpl: UserEmoticonRepositoryImpl

    @Test
    fun `유저 이모티콘 조회`() {
        val userId = generateUserId()
        val emoticonPackId = generatePackId()
        val userEmoticon = jpaDataGenerator.userEmoticonEntityData(userId, emoticonPackId)
        val result = userEmoticonRepositoryImpl.readUserEmoticons(userId)

        assert(result[0].userId == userEmoticon.userId)
        assert(result[0].emoticonPackId == userEmoticon.emoticonPackId)
    }

    private fun generateUserId(): String = UUID.randomUUID().toString()
    private fun generatePackId(): String = UUID.randomUUID().toString()
}
