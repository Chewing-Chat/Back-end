package org.chewing.v1.jpaentity.user

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.hibernate.annotations.DynamicInsert

@DynamicInsert
@Entity
@Table(
    name = "schedule_participant",
)
internal class ScheduleParticipantJpaEntity(
    @EmbeddedId
    private val id: ScheduleParticipantId,
    @Enumerated(EnumType.STRING)
    private var status: ScheduleParticipantStatus,
) : BaseEntity() {
    companion object {
        fun generate(
            userId: String,
            scheduleId: String,
        ): ScheduleParticipantJpaEntity = ScheduleParticipantJpaEntity(
            id = ScheduleParticipantId(userId, scheduleId),
            status = ScheduleParticipantStatus.ACTIVE,
        )
    }
    fun toParticipant(): ScheduleParticipant = ScheduleParticipant.of(
        id.userId,
        id.scheduleId,
        status,
    )
    fun updateStatus(status: ScheduleParticipantStatus) {
        this.status = status
    }
}
