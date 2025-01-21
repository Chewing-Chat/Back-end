package org.chewing.v1.jpaentity.schedule

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.jpaentity.user.ScheduleParticipantId
import org.chewing.v1.model.schedule.ScheduleChangeStatus
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleLog
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert

@Entity
@DynamicInsert
@Table(
    name = "schedule_log",
)
internal class ScheduleLogJpaEntity(
    @EmbeddedId
    private val id: ScheduleParticipantId,
    private val changeStatus: ScheduleChangeStatus,
) : BaseEntity() {
    companion object {
        fun generate(
            userId: UserId,
            scheduleId: ScheduleId,
            changeStatus: ScheduleChangeStatus,
        ): ScheduleLogJpaEntity = ScheduleLogJpaEntity(
            id = ScheduleParticipantId.of(userId, scheduleId),
            changeStatus = changeStatus,
        )
    }

    fun toLog(): ScheduleLog = ScheduleLog.of(
        ScheduleId.of(id.scheduleId),
        UserId.of(id.userId),
        changeStatus,
    )
}
