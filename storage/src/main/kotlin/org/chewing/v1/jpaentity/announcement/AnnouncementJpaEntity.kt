package org.chewing.v1.jpaentity.announcement

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.announcement.Announcement
import org.hibernate.annotations.DynamicInsert
import java.util.UUID

@Entity
@DynamicInsert
@Table(name = "announcement", schema = "chewing")
internal class AnnouncementJpaEntity(
    @Id
    private val announcementId: String = UUID.randomUUID().toString(),
    private val topic: String,
    private val content: String,
) : BaseEntity() {
    companion object {
        fun generate(
            topic: String,
            content: String,
        ): AnnouncementJpaEntity {
            return AnnouncementJpaEntity(
                topic = topic,
                content = content,
            )
        }
    }

    fun toAnnouncement(): Announcement {
        return Announcement.of(
            announcementId,
            topic,
            createdAt,
            content,
        )
    }
}
