package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.announcement.AnnouncementJpaRepository
import org.chewing.v1.repository.jpa.announcement.AnnouncementRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class AnnouncementRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var announcementJpaRepository: AnnouncementJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var announcementRepositoryImpl: AnnouncementRepositoryImpl

    @Test
    fun `공지사항 조회에 성공해야 한다`() {
        val announcement = jpaDataGenerator.announcementEntityData()

        val result = announcementRepositoryImpl.read(announcement.id)

        assert(result != null)
        assert(result!!.topic == announcement.topic)
        assert(result.content == announcement.content)
    }

    @Test
    fun `공지사항 조회에 실패해야 한다`() {
        jpaDataGenerator.announcementEntityData()

        val wrongId = generateAnnouncementId()

        val result = announcementRepositoryImpl.read(wrongId)

        assert(result == null)
    }

    @Test
    fun `공지사항 목록 조회에 성공해야 한다`() {
        jpaDataGenerator.announcementEntityDataList()

        val result = announcementRepositoryImpl.reads()

        assert(result.isNotEmpty())
        val createdAtList = result.map { it.uploadAt }
        assert(createdAtList == createdAtList.sorted())
    }

    private fun generateAnnouncementId(): String = UUID.randomUUID().toString()
}
