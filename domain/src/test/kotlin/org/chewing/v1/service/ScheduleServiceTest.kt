package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.user.schedule.ScheduleAppender
import org.chewing.v1.implementation.user.schedule.ScheduleGenerator
import org.chewing.v1.implementation.user.schedule.ScheduleReader
import org.chewing.v1.implementation.user.schedule.ScheduleRemover
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.repository.user.ScheduleRepository
import org.chewing.v1.service.user.ScheduleService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ScheduleServiceTest {
    private val scheduleRepository: ScheduleRepository = mockk()

    private val scheduleAppender = ScheduleAppender(scheduleRepository)
    private val scheduleReader = ScheduleReader(scheduleRepository)
    private val scheduleRemover = ScheduleRemover(scheduleRepository)
    private val scheduleGenerator = ScheduleGenerator()
    private val scheduleService = ScheduleService(scheduleAppender, scheduleRemover, scheduleReader, scheduleGenerator)

    @Test
    fun `스케줄 추가`() {
        val userId = "userId"
        val scheduleId = "scheduleId"
        val scheduleTime = TestDataFactory.createScheduledTime()
        val scheduleContent = TestDataFactory.createScheduleContent()

        every { scheduleRepository.append(scheduleTime, scheduleContent, userId) } returns scheduleId

        val result = scheduleService.create(userId, scheduleTime, scheduleContent)

        assert(result == scheduleId)
    }

    @Test
    fun `스케줄 삭제`() {
        val scheduleId = "scheduleId"

        every { scheduleRepository.remove(scheduleId) } just Runs

        assertDoesNotThrow {
            scheduleService.delete(scheduleId)
        }
    }

    @Test
    fun `유저 스케줄 삭제`() {
        val userId = "userId"

        every { scheduleRepository.removeUsers(userId) } just Runs

        assertDoesNotThrow {
            scheduleService.deleteUsers(userId)
        }
    }

    @Test
    fun `스케줄 조회`() {
        val userId = "userId"
        val type = ScheduleType.of(2021, 1)
        val schedule = TestDataFactory.createSchedule()

        every { scheduleRepository.reads(userId, type, true) }.returns(listOf(schedule))

        val result = assertDoesNotThrow {
            scheduleService.fetches(userId, type, true)
        }

        assert(result.size == 1)
    }
}
