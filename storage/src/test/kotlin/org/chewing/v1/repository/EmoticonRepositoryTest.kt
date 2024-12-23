package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.emoticon.EmoticonJpaRepository
import org.chewing.v1.jparepository.emoticon.EmoticonPackJpaRepository
import org.chewing.v1.repository.jpa.emoticon.EmoticonRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class EmoticonRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var emoticonJpaRepository: EmoticonJpaRepository

    @Autowired
    private lateinit var emotionPackJpaRepository: EmoticonPackJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var emoticonRepositoryImpl: EmoticonRepositoryImpl

    @Test
    fun `이모티콘 팩 조회 해서 변환 성공`() {
        val emoticonPackInfo = jpaDataGenerator.emoticonPackEntityData()
        val result = emoticonRepositoryImpl.readEmoticonPacks(listOf(emoticonPackInfo.id))

        assert(result.size == 1)
        result.forEach {
            assert(it.id == emoticonPackInfo.id)
            assert(it.name == emoticonPackInfo.name)
            assert(it.url == emoticonPackInfo.url)
        }
    }

    @Test
    fun `이모티콘 조회 해서 변환 성공`() {
        val emoticonPackId = generateEmoticonPackId()
        val emoticonInfo = jpaDataGenerator.emoticonEntityData(emoticonPackId)
        val result = emoticonRepositoryImpl.readEmoticons(listOf(emoticonPackId))

        assert(result.size == 1)
        result.forEach {
            assert(it.emoticonPackId == emoticonInfo.emoticonPackId)
            assert(it.id == emoticonInfo.id)
            assert(it.url == emoticonInfo.url)
        }
    }

    @Test
    fun `이모티콘 리스트 조회`() {
        val emoticonPackId = generateEmoticonPackId()
        jpaDataGenerator.emoticonEntityDataList(emoticonPackId)
        val result = emoticonRepositoryImpl.readEmoticons(listOf(emoticonPackId))
        assert(result.size == 10)
    }

    private fun generateEmoticonPackId() = UUID.randomUUID().toString()
}
