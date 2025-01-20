package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.model.schedule.Schedule
import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class ScheduleEnricher {
    fun enrich(
        userId: UserId,
        scheduleInfos: List<ScheduleInfo>,
        scheduleParticipants: List<ScheduleParticipant>,
    ): List<Schedule> {
        return scheduleInfos.map { scheduleInfo ->
            val participants = scheduleParticipants
                .filter { it.scheduleId == scheduleInfo.id }
                .filter { it.userId != userId }
            Schedule.of(scheduleInfo, participants)
        }
    }
}
