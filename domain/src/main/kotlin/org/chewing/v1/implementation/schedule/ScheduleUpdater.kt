package org.chewing.v1.implementation.schedule

import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.schedule.ScheduleParticipantRepository
import org.chewing.v1.repository.schedule.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleUpdater(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleParticipantRepository: ScheduleParticipantRepository,
) {
    fun updateInfo(scheduleId: ScheduleId, scheduleTime: ScheduleTime, scheduleContent: ScheduleContent) {
        scheduleRepository.update(scheduleId, scheduleTime, scheduleContent)
    }
    fun updateParticipants(scheduleId: ScheduleId, userIds: List<UserId>, status: ScheduleParticipantStatus) {
        scheduleParticipantRepository.updateParticipants(scheduleId, userIds, status)
    }
}
