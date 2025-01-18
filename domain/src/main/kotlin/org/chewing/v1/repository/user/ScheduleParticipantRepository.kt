package org.chewing.v1.repository.user

import org.chewing.v1.model.schedule.ScheduleParticipant
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.springframework.stereotype.Repository

@Repository
interface ScheduleParticipantRepository {
    fun appendParticipants(scheduleId: String, userIds: List<String>)
    fun readsParticipants(scheduleIds: List<String>, status: ScheduleParticipantStatus): List<ScheduleParticipant>
    fun readParticipant(userId: String, scheduleId: String): ScheduleParticipant?
    fun readParticipants(scheduleId: String): List<ScheduleParticipant>
    fun readParticipantScheduleIds(userId: String, status: ScheduleParticipantStatus): List<String>
    fun removeParticipated(userId: String)
    fun removeAllParticipants(scheduleId: String)
    fun removeParticipants(scheduleId: String, userIds: List<String>)
}
