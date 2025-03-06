package org.chewing.v1.repository.schedule

import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantReadStatus
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface ScheduleParticipantRepository {
    fun appendParticipants(scheduleId: ScheduleId, userIds: List<UserId>)
    fun appendOwner(scheduleId: ScheduleId, userId: UserId)
    fun readsParticipants(scheduleIds: List<ScheduleId>, status: ScheduleParticipantStatus): List<ScheduleParticipant>
    fun readParticipant(userId: UserId, scheduleId: ScheduleId): ScheduleParticipant?
    fun readAllParticipants(scheduleId: ScheduleId): List<ScheduleParticipant>
    fun readParticipants(scheduleId: ScheduleId, status: ScheduleParticipantStatus): List<ScheduleParticipant>
    fun readParticipated(userId: UserId, status: ScheduleParticipantStatus): List<ScheduleParticipant>
    fun removeParticipated(userId: UserId): List<ScheduleId>
    fun removeAllParticipants(scheduleId: ScheduleId)
    fun removeParticipants(scheduleId: ScheduleId, userIds: List<UserId>)
    fun removeParticipant(scheduleId: ScheduleId, userId: UserId)
    fun updateParticipantsStatus(scheduleId: ScheduleId, userIds: List<UserId>, status: ScheduleParticipantStatus)
    fun updateParticipantReadStatus(userId: UserId, scheduleId: ScheduleId, status: ScheduleParticipantReadStatus)
}
