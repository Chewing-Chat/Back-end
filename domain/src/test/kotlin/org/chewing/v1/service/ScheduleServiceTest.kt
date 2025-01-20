package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.implementation.user.schedule.ScheduleAppender
import org.chewing.v1.implementation.user.schedule.ScheduleEnricher
import org.chewing.v1.implementation.user.schedule.ScheduleFilter
import org.chewing.v1.implementation.user.schedule.ScheduleGenerator
import org.chewing.v1.implementation.user.schedule.ScheduleReader
import org.chewing.v1.implementation.user.schedule.ScheduleRemover
import org.chewing.v1.implementation.user.schedule.ScheduleUpdater
import org.chewing.v1.implementation.user.schedule.ScheduleValidator
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.repository.user.ScheduleParticipantRepository
import org.chewing.v1.repository.user.ScheduleRepository
import org.chewing.v1.service.user.ScheduleService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class ScheduleServiceTest {
    private val scheduleRepository: ScheduleRepository = mockk()
    private val scheduleParticipantRepository: ScheduleParticipantRepository = mockk()

    private val scheduleAppender = ScheduleAppender(scheduleRepository, scheduleParticipantRepository)
    private val scheduleReader = ScheduleReader(scheduleRepository, scheduleParticipantRepository)
    private val scheduleRemover = ScheduleRemover(scheduleRepository, scheduleParticipantRepository)
    private val scheduleGenerator = ScheduleGenerator()
    private val scheduleEnricher = ScheduleEnricher()
    private val scheduleValidator = ScheduleValidator(scheduleParticipantRepository)
    private val scheduleFilter = ScheduleFilter()
    private val scheduleUpdater = ScheduleUpdater(scheduleRepository)
    private val scheduleService = ScheduleService(
        scheduleAppender,
        scheduleRemover,
        scheduleReader,
        scheduleGenerator,
        scheduleEnricher,
        scheduleValidator,
        scheduleUpdater,
        scheduleFilter,
    )

    @Test
    fun `스케줄 추가 성공`() {
        val userId = TestDataFactory.createUserId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleTime = TestDataFactory.createScheduledTime()
        val scheduleContent = TestDataFactory.createScheduleContent()
        val friendId = TestDataFactory.createFriendId()
        val friendIds = listOf(friendId)

        every { scheduleRepository.append(scheduleTime, scheduleContent) } returns scheduleId
        every { scheduleParticipantRepository.appendParticipants(scheduleId, friendIds.plus(userId)) } just Runs

        val result = scheduleService.create(userId, scheduleTime, scheduleContent, friendIds)

        assert(result == scheduleId)
    }

    @Test
    fun `스케줄 삭제 성공`() {
        val scheduleId = TestDataFactory.createScheduleId()
        val userId = TestDataFactory.createUserId()
        val scheduleParticipant = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.ACTIVE,
        )

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns scheduleParticipant
        every { scheduleRepository.remove(scheduleId) } just Runs
        every { scheduleParticipantRepository.removeAllParticipants(scheduleId) } just Runs

        assertDoesNotThrow {
            scheduleService.delete(userId, scheduleId)
        }
    }

    @Test
    fun `스케줄 삭제 실패 - 참여하지 않는 유저(삭제됨)`() {
        val scheduleId = TestDataFactory.createScheduleId()
        val userId = TestDataFactory.createUserId()
        val scheduleParticipant = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.DELETED,
        )

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns scheduleParticipant

        val result = assertThrows<NotFoundException> {
            scheduleService.delete(userId, scheduleId)
        }

        assert(result.errorCode == ErrorCode.SCHEDULE_NOT_PARTICIPANT)
    }

    @Test
    fun `스케줄 삭제 실패 - 참여하지 않는 유저(존재하지 않음)`() {
        val scheduleId = TestDataFactory.createScheduleId()
        val userId = TestDataFactory.createUserId()

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns null

        val result = assertThrows<NotFoundException> {
            scheduleService.delete(userId, scheduleId)
        }

        assert(result.errorCode == ErrorCode.SCHEDULE_NOT_PARTICIPANT)
    }

    @Test
    fun `스케줄에서 참여 제거`() {
        val userId = TestDataFactory.createUserId()

        every { scheduleParticipantRepository.removeParticipated(userId) } just Runs

        assertDoesNotThrow {
            scheduleService.deleteParticipant(userId)
        }
    }

    @Test
    fun `스케줄 가져오기 - 나 자신은 제외 되어야함`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val targetScheduleType = ScheduleType.of(2021, 1)
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleIds = listOf(scheduleId)
        val scheduleInfos = listOf(
            TestDataFactory.createScheduleInfo(scheduleId = scheduleId, ScheduleStatus.ACTIVE),
        )
        val participants = listOf(
            TestDataFactory.createScheduleParticipant(
                scheduleId = scheduleId,
                userId = userId,
                status = ScheduleParticipantStatus.ACTIVE,
            ),
            TestDataFactory.createScheduleParticipant(
                scheduleId = scheduleId,
                userId = friendId,
                status = ScheduleParticipantStatus.ACTIVE,
            ),
        )

        every {
            scheduleParticipantRepository.readParticipantScheduleIds(
                userId,
                ScheduleParticipantStatus.ACTIVE,
            )
        } returns scheduleIds
        every { scheduleRepository.reads(scheduleIds, targetScheduleType, ScheduleStatus.ACTIVE) } returns scheduleInfos
        every {
            scheduleParticipantRepository.readsParticipants(
                scheduleIds,
                ScheduleParticipantStatus.ACTIVE,
            )
        } returns participants

        val result = scheduleService.fetches(userId, targetScheduleType)

        assert(result.size == 1)
        assert(result[0].participants.size == 1)
    }

    @Test
    fun `스케줄 업데이트 - 새로운 친구가 추가 되었을 떄 성공`() {
        val userId = TestDataFactory.createUserId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleTime = TestDataFactory.createScheduledTime()
        val scheduleContent = TestDataFactory.createScheduleContent()
        val friendId = TestDataFactory.createFriendId()
        val friendIds = listOf(friendId)

        val scheduleParticipant = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.ACTIVE,
        )

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns scheduleParticipant
        every { scheduleRepository.update(scheduleId, scheduleTime, scheduleContent) } just Runs
        every { scheduleParticipantRepository.readParticipants(scheduleId) } returns listOf(scheduleParticipant)
        every { scheduleParticipantRepository.removeParticipants(scheduleId, emptyList()) } just Runs
        every { scheduleParticipantRepository.appendParticipants(scheduleId, friendIds) } just Runs

        assertDoesNotThrow {
            scheduleService.update(userId, scheduleId, scheduleTime, scheduleContent, friendIds)
        }
    }

    @Test
    fun `스케줄 업데이트 - 기존의 친구가 삭제 되었을 떄 성공`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleTime = TestDataFactory.createScheduledTime()
        val scheduleContent = TestDataFactory.createScheduleContent()
        val friendIds = listOf(friendId)
        val userParticipant = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.ACTIVE,
        )
        val friendParticipants = listOf(
            TestDataFactory.createScheduleParticipant(
                userId = friendId,
                scheduleId = scheduleId,
                status = ScheduleParticipantStatus.ACTIVE,
            ),
        )
        val scheduleParticipants = friendParticipants.plus(userParticipant)

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns userParticipant
        every { scheduleRepository.update(scheduleId, scheduleTime, scheduleContent) } just Runs
        every { scheduleParticipantRepository.readParticipants(scheduleId) } returns scheduleParticipants
        every { scheduleParticipantRepository.removeParticipants(scheduleId, friendIds) } just Runs
        every { scheduleParticipantRepository.appendParticipants(scheduleId, emptyList()) } just Runs

        assertDoesNotThrow {
            scheduleService.update(userId, scheduleId, scheduleTime, scheduleContent, emptyList())
        }
    }
}
