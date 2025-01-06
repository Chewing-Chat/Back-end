package org.chewing.v1.repository.jpa.announcement

import org.chewing.v1.jparepository.announcement.AnnouncementJpaRepository
import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.repository.announcement.AnnouncementRepository
import org.springframework.stereotype.Repository

@Repository
internal class AnnouncementRepositoryImpl(
    private val announcementJpaRepository: AnnouncementJpaRepository,
) : AnnouncementRepository {
    override fun reads(): List<Announcement> = announcementJpaRepository.findByOrderByCreatedAt().map {
        it.toAnnouncement()
    }

    override fun read(announcementId: String): Announcement? = announcementJpaRepository.findByAnnouncementId(announcementId)?.toAnnouncement()
}
