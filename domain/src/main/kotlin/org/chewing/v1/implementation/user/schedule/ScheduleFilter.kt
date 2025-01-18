package org.chewing.v1.implementation.user.schedule

import org.springframework.stereotype.Component

@Component
class ScheduleFilter {
    fun filterRemovesTarget(participantIds: List<String>, targetIds: List<String>): List<String> {
        return participantIds.filter { it !in targetIds }
    }
    fun filterAppendsTarget(participantIds: List<String>, targetIds: List<String>): List<String> {
        return targetIds.filter { it !in participantIds }
    }
}
