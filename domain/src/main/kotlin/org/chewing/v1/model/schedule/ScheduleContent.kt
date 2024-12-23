package org.chewing.v1.model.schedule

class ScheduleContent private constructor(
    val title: String,
    val memo: String,
    val location: String,
    val private: Boolean,
) {
    companion object {
        fun of(
            title: String,
            memo: String,
            location: String,
            private: Boolean,
        ): ScheduleContent {
            return ScheduleContent(
                title,
                memo,
                location,
                private,
            )
        }
    }
}
