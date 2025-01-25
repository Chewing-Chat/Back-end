package org.chewing.v1.dto.request.user

import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleRequest {
    data class Delete(
        val scheduleId: String
    ) {
        fun toScheduleId(): String = scheduleId
    }

    data class Cancel(
        val scheduleId: String
    ) {
        fun toScheduleId(): String = scheduleId
    }

    data class Create(
        val title: String,
        val friendIds: List<String>,
        val dateTime: String,
        val timeDecided: Boolean,
        val memo: String,
        val location: String,
    ) {
        fun toScheduleContent(): ScheduleContent = ScheduleContent.of(title, memo, location)

        fun toScheduleTime(): ScheduleTime {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val date = LocalDateTime.parse(dateTime, formatter)
            return ScheduleTime.of(date, timeDecided)
        }

        fun toFriendIds(): List<UserId> = friendIds.map { UserId.of(it) }
    }

    data class Update(
        val scheduleId: String,
        val title: String,
        val friendIds: List<String>,
        val dateTime: String,
        val timeDecided: Boolean,
        val memo: String,
        val location: String,
        val participated: Boolean,
    ) {
        fun toScheduleId(): ScheduleId = ScheduleId.of(scheduleId)

        fun toScheduleContent(): ScheduleContent = ScheduleContent.of(title, memo, location)

        fun toScheduleTime(): ScheduleTime {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val date = LocalDateTime.parse(dateTime, formatter)
            return ScheduleTime.of(date, timeDecided)
        }

        fun toFriendIds(): List<UserId> = friendIds.map { UserId.of(it) }

        fun toParticipated(): Boolean = participated
    }
}
