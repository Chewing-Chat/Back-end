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
        val announcementId = TestDataFactory.createAnnouncementId()
        val announcement = TestDataFactory.createAnnouncement(announcementId)
        val announcements = listOf(announcement)

        every { announcementRepository.reads() } returns announcements

        val result = announcementService.readAnnouncements()

        assert(result.size == announcements.size)
        result.forEach {
            assert(it.announcementId == announcementId)
        }
    }

    @Test
    fun `공지사항 세부 읽기 테스트 - 성공`() {
        val announcementId = TestDataFactory.createAnnouncementId()
        val announcement = TestDataFactory.createAnnouncement(announcementId)

        every { announcementRepository.read(announcementId) } returns announcement

        val result = announcementService.readAnnouncement(announcementId)

        assert(result.announcementId == announcementId)
    }

    @Test
    fun `공지사항 세부 읽기 테스트 - 실패`() {
        val announcementId = TestDataFactory.createAnnouncementId()

        every { announcementRepository.read(announcementId) }.returns(null)

        val exception = assertThrows<NotFoundException> {
            announcementService.readAnnouncement(announcementId)
        }

        assert(exception.errorCode == ErrorCode.ANNOUNCEMENT_NOT_FOUND)
    }
}
