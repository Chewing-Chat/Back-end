package org.chewing.v1.service.announcement

import org.chewing.v1.implementation.announcement.AnnouncementReader
import org.chewing.v1.model.announcement.Announcement
import org.springframework.stereotype.Service

@Service
class AnnouncementService(
    private val announcementReader: AnnouncementReader,
) {
    fun readAnnouncements(): List<Announcement> {
        return announcementReader.reads()
    }

    fun readAnnouncement(announcementId: String): Announcement {
        return announcementReader.read(announcementId)
    }
}
