package org.chewing.v1.implementation.schedule

import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.schedule.ScheduleLogRepository
import org.chewing.v1.repository.schedule.ScheduleParticipantRepository
import org.chewing.v1.repository.schedule.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleAppender(
    val scheduleRepository: ScheduleRepository,
    val scheduleParticipantRepository: ScheduleParticipantRepository,
    val scheduleLogRepository: ScheduleLogRepository,
) {
    fun appendInfo(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent): ScheduleId {
        return scheduleRepository.append(scheduleTime, scheduleContent)
    }

    fun appendParticipants(scheduleId: ScheduleId, userIds: List<UserId>) {
        scheduleParticipantRepository.appendParticipants(scheduleId, userIds)
    }

    fun appendOwner(scheduleId: ScheduleId, userId: UserId) {
        scheduleParticipantRepository.appendOwner(scheduleId, userId)
    }

    fun appendLog(scheduleId: ScheduleId, userId: UserId, action: ScheduleAction) {
        scheduleLogRepository.appendLog(scheduleId, userId, action)
    }
}
