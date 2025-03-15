package org.chewing.v1.jpaentity.schedule

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.jpaentity.user.ScheduleParticipantId
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantReadStatus
import org.chewing.v1.model.schedule.ScheduleParticipantRole
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert

@DynamicInsert
@Entity
@Table(
    name = "schedule_participant",
    schema = "chewing",
    indexes = [
        Index(name = "schedule_participant_idx_user_id_status", columnList = "user_id, status"),
        Index(name = "schedule_participant_idx_schedule_id_status", columnList = "schedule_id, status"),
    ],
)
internal class ScheduleParticipantJpaEntity(
    @EmbeddedId
    private val id: ScheduleParticipantId,
    @Enumerated(EnumType.STRING)
    private var status: ScheduleParticipantStatus,
    @Enumerated(EnumType.STRING)
    private var role: ScheduleParticipantRole,
    @Enumerated(EnumType.STRING)
    private var readStatus: ScheduleParticipantReadStatus,
) : BaseEntity() {
    companion object {
        fun generate(
            userId: UserId,
            scheduleId: ScheduleId,
            role: ScheduleParticipantRole,
        ): ScheduleParticipantJpaEntity = ScheduleParticipantJpaEntity(
            id = ScheduleParticipantId.Companion.of(userId, scheduleId),
            status = ScheduleParticipantStatus.ACTIVE,
            role = role,
            readStatus = ScheduleParticipantReadStatus.UNREAD,
        )
    }
    fun toParticipant(): ScheduleParticipant = ScheduleParticipant.Companion.of(
        UserId.Companion.of(id.userId),
        ScheduleId.Companion.of(id.scheduleId),
        status,
        role,
        readStatus,
    )
    fun updateStatus(status: ScheduleParticipantStatus) {
        this.status = status
    }
    fun updateReadStatus(readStatus: ScheduleParticipantReadStatus) {
        this.readStatus = readStatus
    }
}
