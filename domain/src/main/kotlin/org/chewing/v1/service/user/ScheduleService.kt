package org.chewing.v1.service.user

import org.chewing.v1.implementation.user.schedule.ScheduleAppender
import org.chewing.v1.implementation.user.schedule.ScheduleGenerator
import org.chewing.v1.implementation.user.schedule.ScheduleReader
import org.chewing.v1.implementation.user.schedule.ScheduleRemover
import org.chewing.v1.model.schedule.*
import org.springframework.stereotype.Service

@Service
class ScheduleService(
    private val scheduleAppender: ScheduleAppender,
    private val scheduleRemover: ScheduleRemover,
    private val scheduleReader: ScheduleReader,
    private val scheduleGenerator: ScheduleGenerator,
) {
    fun create(userId: String, scheduleTime: ScheduleTime, scheduleContent: ScheduleContent): String {
        return scheduleAppender.append(scheduleTime, scheduleContent, userId)
    }

    fun delete(scheduleId: String) {
        scheduleRemover.remove(scheduleId)
    }

    fun deleteUsers(userId: String) {
        scheduleRemover.removeUsers(userId)
    }

    fun fetches(userId: String, type: ScheduleType, isOwned: Boolean): List<Schedule> =
        scheduleReader.reads(userId, type, isOwned)

    fun createAiSchedule(userId: String, scheduleStringInfo: String): String {
        val (scheduleContent, scheduleTime) = scheduleGenerator.generateScheduleFromString(scheduleStringInfo)
        return scheduleAppender.append(scheduleTime, scheduleContent, userId)
    }
}
