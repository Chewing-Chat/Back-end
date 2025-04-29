package org.chewing.v1.model.report

class Report(
    val targetType: ReportTargetType,
    val reason: String,
) {
    companion object {
        fun of(targetType: ReportTargetType, reason: String): Report {
            return Report(targetType, reason)
        }
    }
}
