package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.ScheduleParticipantRepository
import org.chewing.v1.repository.user.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleReader(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleParticipantRepository: ScheduleParticipantRepository,
) {
    fun readsParticipants(scheduleIds: List<String>, status: ScheduleParticipantStatus): List<ScheduleParticipant> {
        return scheduleParticipantRepository.readsParticipants(scheduleIds, status)
    }

    fun readInfos(scheduleIds: List<String>, type: ScheduleType, status: ScheduleStatus): List<ScheduleInfo> {
        return scheduleRepository.reads(scheduleIds, type, status)
    }

    fun readParticipants(scheduleId: String): List<ScheduleParticipant> {
        return scheduleParticipantRepository.readParticipants(scheduleId)
    }

    fun readParticipantScheduleIds(userId: UserId, status: ScheduleParticipantStatus): List<String> {
        return scheduleParticipantRepository.readParticipantScheduleIds(userId, status)
    }
}
