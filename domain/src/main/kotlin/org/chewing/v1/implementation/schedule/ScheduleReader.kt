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

    fun readParticipants(scheduleId: ScheduleId): List<ScheduleParticipant> {
        return scheduleParticipantRepository.readParticipants(scheduleId)
    }

    fun readParticipantScheduleIds(userId: UserId, status: ScheduleParticipantStatus): List<ScheduleId> {
        return scheduleParticipantRepository.readParticipantScheduleIds(userId, status)
    }

    fun readLogs(userId: UserId): List<ScheduleLog> {
        return scheduleLogRepository.readLogs(userId)
    }
}
