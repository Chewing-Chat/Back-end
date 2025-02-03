package org.chewing.v1.model.schedule

@JvmInline
value class ScheduleId private constructor(
    val id: String,
) {
    companion object {
        fun of(
            id: String,
        ): ScheduleId {
            return ScheduleId(id)
        }
    }
}
