package org.chewing.v1.jpaentity.schedule

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleLog
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert
import java.util.UUID

@Entity
@DynamicInsert
@Table(
    name = "schedule_log",
)
internal class ScheduleLogJpaEntity(
    @Id
    private val scheduleLogId: String = UUID.randomUUID().toString(),
    private val scheduleId: String,
    private val userId: String,
    @Enumerated(EnumType.STRING)
    private val action: ScheduleAction,
) : BaseEntity() {
    companion object {
        fun generate(
            userId: UserId,
            scheduleId: ScheduleId,
            action: ScheduleAction,
        ): ScheduleLogJpaEntity = ScheduleLogJpaEntity(
            scheduleId = scheduleId.id,
            userId = userId.id,
            action = action,
        )
    }

    fun toLog(): ScheduleLog = ScheduleLog.of(
        ScheduleId.of(scheduleId),
        UserId.of(userId),
        action,
        createdAt,
    )
}
