package org.chewing.v1.repository.user

import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface ScheduleParticipantRepository {
    fun appendParticipants(scheduleId: String, userIds: List<UserId>)
    fun readsParticipants(scheduleIds: List<String>, status: ScheduleParticipantStatus): List<ScheduleParticipant>
    fun readParticipant(userId: UserId, scheduleId: String): ScheduleParticipant?
    fun readParticipants(scheduleId: String): List<ScheduleParticipant>
    fun readParticipantScheduleIds(userId: UserId, status: ScheduleParticipantStatus): List<String>
    fun removeParticipated(userId: UserId)
    fun removeAllParticipants(scheduleId: String)
    fun removeParticipants(scheduleId: String, userIds: List<UserId>)
}
