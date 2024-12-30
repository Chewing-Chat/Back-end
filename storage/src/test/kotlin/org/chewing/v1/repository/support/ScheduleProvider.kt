package org.chewing.v1.repository.support

import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleTime
import java.time.LocalDateTime

object ScheduleProvider {
    fun buildContent(): ScheduleContent {
        return ScheduleContent.of("title", "content", "location")
    }

    fun buildTime(): ScheduleTime {
        return ScheduleTime.of(LocalDateTime.now(), true)
    }

    fun build1000YearTime(): ScheduleTime {
        return ScheduleTime.of(
            LocalDateTime.of(1000, 1, 1, 0, 0, 0),
            true,
        )
    }
}
