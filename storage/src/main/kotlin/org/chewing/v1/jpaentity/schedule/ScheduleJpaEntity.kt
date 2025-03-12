package org.chewing.v1.jpaentity.schedule

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.persistence.*
import java.util.*
import org.chewing.v1.model.schedule.*
import java.time.LocalDateTime
@Entity
@Table(
    name = "schedule",
    indexes = [
        Index(name = "schedule_idx_dateTime", columnList = "dateTime"),
    ],
)
internal class ScheduleJpaEntity(
    @Id
    private val scheduleId: String = UUID.randomUUID().toString(),
    private var name: String,
    private var content: String,
    private var dateTime: LocalDateTime,
    private var timeDecided: Boolean,
    private var location: String,
    @Enumerated(EnumType.STRING)
    private var status: ScheduleStatus,
    @Version
    private var version: Long? = 0,
) {
    companion object {
        fun generate(
            scheduleContent: ScheduleContent,
            scheduleTime: ScheduleTime,
        ): ScheduleJpaEntity = ScheduleJpaEntity(
            name = scheduleContent.title,
            content = scheduleContent.memo,
            dateTime = scheduleTime.dateTime,
            location = scheduleContent.location,
            timeDecided = scheduleTime.timeDecided,
            status = ScheduleStatus.ACTIVE,
        )
    }

    fun toScheduleInfo(): ScheduleInfo = ScheduleInfo.of(
        ScheduleId.of(scheduleId),
        name,
        content,
        dateTime,
        location,
        timeDecided,
        status,
    )

    fun updateStatus(status: ScheduleStatus) {
        this.status = status
    }

    fun updateInfo(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent) {
        this.name = scheduleContent.title
        this.content = scheduleContent.memo
        this.dateTime = scheduleTime.dateTime
        this.location = scheduleContent.location
        this.timeDecided = scheduleTime.timeDecided
    }
}
