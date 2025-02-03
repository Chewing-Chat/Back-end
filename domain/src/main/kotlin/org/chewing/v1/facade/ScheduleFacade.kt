package org.chewing.v1.facade

import org.chewing.v1.service.user.ScheduleService
import org.springframework.stereotype.Service

@Service
class ScheduleFacade(
    private val scheduleService: ScheduleService,
)
