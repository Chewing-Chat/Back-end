package org.chewing.v1.implementation.announcement

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.repository.announcement.AnnouncementRepository
import org.springframework.stereotype.Component

@Component
class AnnouncementReader(
    private val announcementRepository: AnnouncementRepository,
) {
    fun reads(): List<Announcement> {
        return announcementRepository.reads()
    }

    fun read(announcementId: String): Announcement {
        return announcementRepository.read(announcementId)
            ?: throw NotFoundException(ErrorCode.ANNOUNCEMENT_NOT_FOUND)
    }
}
