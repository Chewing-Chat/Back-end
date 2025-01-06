package org.chewing.v1.repository.announcement

import org.chewing.v1.model.announcement.Announcement

interface AnnouncementRepository {
    fun reads(): List<Announcement>
    fun read(announcementId: String): Announcement?
}
