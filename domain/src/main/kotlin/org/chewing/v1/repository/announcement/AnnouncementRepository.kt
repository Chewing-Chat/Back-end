package org.chewing.v1.repository.announcement

import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.announcement.AnnouncementId

interface AnnouncementRepository {
    fun reads(): List<Announcement>
    fun read(announcementId: AnnouncementId): Announcement?
}
