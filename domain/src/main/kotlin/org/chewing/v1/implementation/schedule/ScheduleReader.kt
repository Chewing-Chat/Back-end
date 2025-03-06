package org.chewing.v1.implementation.schedule

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleInfo
import org.chewing.v1.model.schedule.ScheduleLog
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.schedule.ScheduleLogRepository
import org.chewing.v1.repository.schedule.ScheduleParticipantRepository
import org.chewing.v1.repository.schedule.ScheduleRepository
import org.springframework.stereotype.Component

@Component
class ScheduleReader(
    private val scheduleRepository: ScheduleRepository,
    private val scheduleParticipantRepository: ScheduleParticipantRepository,
    private val scheduleLogRepository: ScheduleLogRepository,
) {
    fun readsParticipants(scheduleIds: List<ScheduleId>, status: ScheduleParticipantStatus): List<ScheduleParticipant> {
        return scheduleParticipantRepository.readsParticipants(scheduleIds, status)
    }

    fun readInfos(scheduleIds: List<ScheduleId>, type: ScheduleType, status: ScheduleStatus): List<ScheduleInfo> {
        return scheduleRepository.reads(scheduleIds, type, status)
    }

    fun readInfo(scheduleId: ScheduleId, status: ScheduleStatus): ScheduleInfo {
        return scheduleRepository.read(scheduleId, status) ?: throw NotFoundException(ErrorCode.SCHEDULE_NOT_FOUND)
    }

    fun readAllParticipants(scheduleId: ScheduleId): List<ScheduleParticipant> {
        return scheduleParticipantRepository.readAllParticipants(scheduleId)
    }

    fun readParticipants(scheduleId: ScheduleId, status: ScheduleParticipantStatus): List<ScheduleParticipant> {
        return scheduleParticipantRepository.readParticipants(scheduleId, status)
    }

    fun readParticipated(userId: UserId, status: ScheduleParticipantStatus): List<ScheduleParticipant> {
        return scheduleParticipantRepository.readParticipated(userId, status)
    }

    fun readsLogs(scheduleIds: List<ScheduleId>): List<ScheduleLog> {
        return scheduleLogRepository.readsLogs(scheduleIds)
    }
}
