package org.chewing.v1.repository.user

import org.chewing.v1.model.schedule.*
import org.springframework.stereotype.Repository

@Repository
interface ScheduleRepository {
    fun append(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent, userId: String): String
    fun remove(scheduleId: String)
    fun reads(userId: String, type: ScheduleType, isOwned: Boolean): List<Schedule>
    fun removeUsers(userId: String)
}
