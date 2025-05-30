package org.chewing.v1.repository.jpa.announcement

import org.chewing.v1.jparepository.announcement.AnnouncementJpaRepository
import org.chewing.v1.util.SortType
import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.announcement.AnnouncementId
import org.chewing.v1.repository.announcement.AnnouncementRepository
import org.springframework.stereotype.Repository

@Repository
internal class AnnouncementRepositoryImpl(
    private val announcementJpaRepository: AnnouncementJpaRepository,
) : AnnouncementRepository {
    override fun reads(): List<Announcement> = announcementJpaRepository.findAll(SortType.LATEST.toSort()).map {
        it.toAnnouncement()
    }

    override fun read(announcementId: AnnouncementId): Announcement? = announcementJpaRepository.findByAnnouncementId(announcementId.id)?.toAnnouncement()
}
