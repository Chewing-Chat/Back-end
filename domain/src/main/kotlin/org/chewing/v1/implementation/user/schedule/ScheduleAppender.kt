package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.repository.user.ScheduleParticipantRepository
import org.chewing.v1.repository.user.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleAppender(
    val scheduleRepository: ScheduleRepository,
    val scheduleParticipantRepository: ScheduleParticipantRepository,
) {
    fun appendInfo(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent): String {
        return scheduleRepository.append(scheduleTime, scheduleContent)
    }

    fun appendParticipants(scheduleId: String, userIds: List<String>) {
        scheduleParticipantRepository.appendParticipants(scheduleId, userIds)
    }
}
