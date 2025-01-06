package org.chewing.v1.service

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.implementation.announcement.AnnouncementReader
import org.chewing.v1.repository.announcement.AnnouncementRepository
import org.chewing.v1.service.announcement.AnnouncementService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AnnouncementServiceTest {
    private val announcementRepository: AnnouncementRepository = mockk()
    private val announcementReader = AnnouncementReader(announcementRepository)
    private val announcementService = AnnouncementService(announcementReader)

    @Test
    fun `공지사항 목록 읽기 테스트`() {
        val announcementId1 = "announcementId1"
        val announcementId2 = "announcementId2"
        val announcement1 = TestDataFactory.createAnnouncement(announcementId1)
        val announcement2 = TestDataFactory.createAnnouncement(announcementId2)

        every { announcementRepository.reads() } returns listOf(announcement1, announcement2)

        val result = announcementService.readAnnouncements()

        assert(result.size == 2)
        assert(result[0].id == announcementId1)
        assert(result[1].id == announcementId2)
    }

    @Test
    fun `공지사항 세부 읽기 테스트 - 성공`() {
        val announcementId = "announcementId"
        val announcement = TestDataFactory.createAnnouncement(announcementId)

        every { announcementRepository.read(announcementId) } returns announcement

        val result = announcementService.readAnnouncement(announcementId)

        assert(result.id == announcementId)
    }

    @Test
    fun `공지사항 세부 읽기 테스트 - 실패`() {
        val announcementId = "announcementId"

        every { announcementRepository.read(announcementId) }.returns(null)

        val exception = assertThrows<NotFoundException> {
            announcementService.readAnnouncement(announcementId)
        }

        assert(exception.errorCode == ErrorCode.ANNOUNCEMENT_NOT_FOUND)
    }
}
