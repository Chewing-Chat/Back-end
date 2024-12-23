package org.chewing.v1.repository.support

import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleTime
import java.time.LocalDateTime

object ScheduleProvider {
    fun buildContent(isPrivate: Boolean): ScheduleContent {
        return ScheduleContent.of("title", "content", "location", isPrivate)
    }

    fun buildTime(): ScheduleTime {
        return ScheduleTime.of(LocalDateTime.now(), LocalDateTime.now().plusMinutes(5), LocalDateTime.now())
    }

    fun build1000YearFirstTime(): ScheduleTime {
        return ScheduleTime.of(
            LocalDateTime.of(1000, 1, 1, 0, 0, 0),
            LocalDateTime.of(1000, 12, 31, 23, 59, 59),
            LocalDateTime.now(),
        )
    }

    fun build1000YearSecondTime(): ScheduleTime {
        return ScheduleTime.of(
            LocalDateTime.of(1000, 1, 2, 0, 0, 0),
            LocalDateTime.of(1000, 12, 31, 23, 59, 59),
            LocalDateTime.now(),
        )
    }
}
