package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.user.ScheduleJpaRepository
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.repository.jpa.user.ScheduleRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.repository.support.ScheduleProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.temporal.ChronoUnit
import java.util.UUID

class ScheduleRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var scheduleJpaRepository: ScheduleJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var scheduleRepositoryImpl: ScheduleRepositoryImpl

    @Test
    fun `스케줄 저장에 성공`() {
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.buildTime()
        val result = scheduleRepositoryImpl.append(time, content)
        assert(result.isNotEmpty())
    }

    @Test
    fun `스케줄 삭제에 성공 - 물리적으로 삭제는 하지 않고 논리적으로 DELETE`() {
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.buildTime()
        val schedule = jpaDataGenerator.scheduleEntityData(content, time)
        scheduleRepositoryImpl.remove(schedule.id)
        val result = scheduleJpaRepository.findById(schedule.id)
        assert(result.isPresent)
        assert(result.get().toScheduleInfo().status == ScheduleStatus.DELETED)
    }

    @Test
    fun `스케줄 정보 수정에 성공`() {
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.buildTime()
        val schedule = jpaDataGenerator.scheduleEntityData(content, time)
        val newContent = ScheduleProvider.buildNewContent()
        val newTime = ScheduleProvider.buildNewTime()
        scheduleRepositoryImpl.update(schedule.id, newTime, newContent)
        val result = scheduleJpaRepository.findById(schedule.id)
        assert(result.isPresent)
        assert(result.get().toScheduleInfo().content.memo == newContent.memo)
        assert(ChronoUnit.SECONDS.between(result.get().toScheduleInfo().time.dateTime, newTime.dateTime) <= 1)
        assert(result.get().toScheduleInfo().time.timeDecided == newTime.timeDecided)
        assert(result.get().toScheduleInfo().content.title == newContent.title)
        assert(result.get().toScheduleInfo().content.location == newContent.location)
        assert(result.get().toScheduleInfo().id == schedule.id)
    }

    @Test
    fun `시간 기준으로 스케줄 조회에 성공`() {
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.build1000YearTime()
        val schedule = jpaDataGenerator.scheduleEntityData(content, time)
        val schedules = scheduleRepositoryImpl.reads(
            listOf(schedule.id),
            ScheduleType.of(time.dateTime.year, time.dateTime.monthValue),
            ScheduleStatus.ACTIVE,
        )
        assert(schedules.size == 1)
        assert(schedule.time.dateTime == schedules[0].time.dateTime)
    }

    @Test
    fun `시간 기준으로 스케줄 조회에 실패 - 삭제된 스케줄`() {
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.build1000YearTime()
        val schedule = jpaDataGenerator.scheduleDeleteEntityData(content, time)
        val schedules = scheduleRepositoryImpl.reads(
            listOf(schedule.id),
            ScheduleType.of(time.dateTime.year, time.dateTime.monthValue),
            ScheduleStatus.ACTIVE,
        )
        assert(schedules.isEmpty())
    }

    @Test
    fun `시간 기준으로 스케줄 조회에 실패`() {
        val content = ScheduleProvider.buildContent()
        val time = ScheduleProvider.build1000YearTime()
        val schedule1 = jpaDataGenerator.scheduleEntityData(content, time)
        val schedule2 = jpaDataGenerator.scheduleEntityData(content, time)
        val schedules = scheduleRepositoryImpl.reads(
            listOf(
                schedule1.id,
                schedule2.id,
            ),
            ScheduleType.of(time.dateTime.year.minus(1), time.dateTime.monthValue),
            ScheduleStatus.ACTIVE,
        )
        assert(schedules.isEmpty())
    }

    fun generateUserId(): String = UUID.randomUUID().toString()
}
