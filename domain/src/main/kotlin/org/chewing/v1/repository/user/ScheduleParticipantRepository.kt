package org.chewing.v1.repository.user

import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface ScheduleParticipantRepository {
    fun appendParticipants(scheduleId: ScheduleId, userIds: List<UserId>)
    fun readsParticipants(scheduleIds: List<ScheduleId>, status: ScheduleParticipantStatus): List<ScheduleParticipant>
    fun readParticipant(userId: UserId, scheduleId: ScheduleId): ScheduleParticipant?
    fun readParticipants(scheduleId: ScheduleId): List<ScheduleParticipant>
    fun readParticipantScheduleIds(userId: UserId, status: ScheduleParticipantStatus): List<ScheduleId>
    fun removeParticipated(userId: UserId)
    fun removeAllParticipants(scheduleId: ScheduleId)
    fun removeParticipants(scheduleId: ScheduleId, userIds: List<UserId>)
}
