package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.user.ScheduleJpaEntity
import org.chewing.v1.jparepository.user.ScheduleJpaRepository
import org.chewing.v1.model.schedule.*
import org.chewing.v1.repository.user.ScheduleRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@Repository
internal class ScheduleRepositoryImpl(
    private val scheduleJpaRepository: ScheduleJpaRepository,
) : ScheduleRepository {
    override fun append(scheduleTime: ScheduleTime, scheduleContent: ScheduleContent, userId: String): String {
        return scheduleJpaRepository.save(ScheduleJpaEntity.generate(scheduleContent, scheduleTime, userId)).toSchedule().id
    }

    @Transactional
    override fun remove(scheduleId: String) {
        scheduleJpaRepository.deleteById(scheduleId)
    }

    @Transactional
    override fun removeUsers(userId: String) {
        scheduleJpaRepository.deleteAllByUserId(userId)
    }

    override fun reads(userId: String, type: ScheduleType, isOwned: Boolean): List<Schedule> {
        val startDateTime = LocalDateTime.of(type.year, type.month, 1, 0, 0)
        val endDateTime = startDateTime
            .with(TemporalAdjusters.firstDayOfNextMonth()) // 다음 달의 첫 날로 설정
            .minusSeconds(1) // 1초 전으로 설정
        return scheduleJpaRepository.findSchedules(userId, startDateTime, endDateTime, isOwned)
            .map { it.toSchedule() }
    }
}
