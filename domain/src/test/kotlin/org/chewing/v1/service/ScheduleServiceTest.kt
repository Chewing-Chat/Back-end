package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.implementation.schedule.ScheduleAppender
import org.chewing.v1.implementation.schedule.ScheduleEnricher
import org.chewing.v1.implementation.schedule.ScheduleFilter
import org.chewing.v1.implementation.schedule.ScheduleReader
import org.chewing.v1.implementation.schedule.ScheduleRemover
import org.chewing.v1.implementation.schedule.ScheduleUpdater
import org.chewing.v1.implementation.schedule.ScheduleValidator
import org.chewing.v1.util.OptimisticLockHandler
import org.chewing.v1.model.schedule.ScheduleParticipantReadStatus
import org.chewing.v1.model.schedule.ScheduleParticipantRole
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.repository.schedule.ScheduleLogRepository
import org.chewing.v1.repository.schedule.ScheduleParticipantRepository
import org.chewing.v1.repository.schedule.ScheduleRepository
import org.chewing.v1.service.user.ScheduleService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.OptimisticLockingFailureException

class ScheduleServiceTest {
    private val scheduleRepository: ScheduleRepository = mockk()
    private val scheduleParticipantRepository: ScheduleParticipantRepository = mockk()
    private val scheduleLogRepository: ScheduleLogRepository = mockk()
    private val optimisticLockHandler = OptimisticLockHandler()

    private val scheduleAppender =
        ScheduleAppender(scheduleRepository, scheduleParticipantRepository, scheduleLogRepository)
    private val scheduleReader =
        ScheduleReader(scheduleRepository, scheduleParticipantRepository, scheduleLogRepository)
    private val scheduleRemover = ScheduleRemover(scheduleRepository, scheduleParticipantRepository)
    private val scheduleEnricher = ScheduleEnricher()
    private val scheduleValidator = ScheduleValidator(scheduleParticipantRepository)
    private val scheduleFilter = ScheduleFilter()
    private val scheduleUpdater = ScheduleUpdater(optimisticLockHandler, scheduleRepository, scheduleParticipantRepository)
    private val scheduleService = ScheduleService(
        scheduleAppender,
        scheduleRemover,
        scheduleReader,
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
        every { scheduleParticipantRepository.appendParticipants(scheduleId, friendIds) } just Runs
        every { scheduleParticipantRepository.appendOwner(scheduleId, userId) } just Runs
        every { scheduleLogRepository.appendLog(scheduleId, userId, any()) } just Runs

        val result = scheduleService.create(userId, scheduleTime, scheduleContent, friendIds)

        assert(result == scheduleId)
    }

    @Test
    fun `스케줄 삭제 성공 - 요청자가 소유자여야함`() {
        val scheduleId = TestDataFactory.createScheduleId()
        val userId = TestDataFactory.createUserId()
        val scheduleParticipant = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.ACTIVE,
            role = ScheduleParticipantRole.OWNER,
            readStatus = ScheduleParticipantReadStatus.READ,
        )

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns scheduleParticipant
        every { scheduleRepository.remove(scheduleId) } just Runs
        every { scheduleParticipantRepository.removeAllParticipants(scheduleId) } just Runs
        every { scheduleLogRepository.appendLog(scheduleId, userId, any()) } just Runs

        assertDoesNotThrow {
            scheduleService.delete(userId, scheduleId)
        }
    }

    @Test
    fun `스케줄 삭제 실패 - 소유자가 아닌 경우(오로지 참여자)`() {
        val scheduleId = TestDataFactory.createScheduleId()
        val userId = TestDataFactory.createUserId()
        val scheduleParticipant = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.DELETED,
            role = ScheduleParticipantRole.PARTICIPANT,
            readStatus = ScheduleParticipantReadStatus.READ,
        )

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns scheduleParticipant

        val result = assertThrows<ConflictException> {
            scheduleService.delete(userId, scheduleId)
        }

        assert(result.errorCode == ErrorCode.SCHEDULE_NOT_OWNER)
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
    fun `스케줄에서 참여 제거(자기의 모든 스케줄 참여 취소)`() {
        val userId = TestDataFactory.createUserId()
        val scheduleId = TestDataFactory.createScheduleId()

        every { scheduleParticipantRepository.removeParticipated(userId) } returns listOf(scheduleId)
        every { scheduleLogRepository.appendLog(scheduleId, userId, any()) } just Runs

        assertDoesNotThrow {
            scheduleService.deleteAllParticipant(userId)
        }
    }

    @Test
    fun `스케줄 목록 가져오기 - 나 자신은 제외 되고 소유자이자 참여자라면 isParticipate,isOwned를 리턴함 - 한번은 초대 되어야함 `() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val targetScheduleType = ScheduleType.of(2021, 1)
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleInfos = listOf(
            TestDataFactory.createScheduleInfo(scheduleId = scheduleId, ScheduleStatus.ACTIVE),
        )

        val scheduleParticipated = TestDataFactory.createScheduleParticipant(
            userId,
            scheduleId,
            ScheduleParticipantStatus.ACTIVE,
            ScheduleParticipantRole.OWNER,
            readStatus = ScheduleParticipantReadStatus.UNREAD,
        )

        val participants = listOf(
            TestDataFactory.createScheduleParticipant(
                scheduleId = scheduleId,
                userId = userId,
                status = ScheduleParticipantStatus.ACTIVE,
                role = ScheduleParticipantRole.OWNER,
                readStatus = ScheduleParticipantReadStatus.READ,
            ),
            TestDataFactory.createScheduleParticipant(
                scheduleId = scheduleId,
                userId = friendId,
                status = ScheduleParticipantStatus.ACTIVE,
                role = ScheduleParticipantRole.PARTICIPANT,
                readStatus = ScheduleParticipantReadStatus.READ,
            ),
        )

        every { scheduleParticipantRepository.readParticipated(any(), any()) } returns listOf(scheduleParticipated)
        every { scheduleRepository.reads(any(), any(), any()) } returns scheduleInfos
        every { scheduleParticipantRepository.readsParticipants(any(), any()) } returns participants

        val (schedules, count) = scheduleService.fetches(userId, targetScheduleType)

        assert(schedules.size == 1)
        assert(schedules[0].participants.size == 1)
        assert(schedules[0].isOwned)
        assert(schedules[0].isParticipant)

        assert(count == 1)
    }

    @Test
    fun `스케줄 목록 가져오기 - 나 자신은 제외되고 소유자 참여자 모두 아니라면 isOwned False를 리턴함 - 한번은 초대 되어야함`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val targetScheduleType = ScheduleType.of(2021, 1)
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleInfos = listOf(
            TestDataFactory.createScheduleInfo(scheduleId = scheduleId, ScheduleStatus.ACTIVE),
        )
        val scheduleParticipated = TestDataFactory.createScheduleParticipant(
            userId,
            scheduleId,
            ScheduleParticipantStatus.ACTIVE,
            ScheduleParticipantRole.OWNER,
            readStatus = ScheduleParticipantReadStatus.UNREAD,
        )
        val participants = listOf(
            TestDataFactory.createScheduleParticipant(
                scheduleId = scheduleId,
                userId = userId,
                status = ScheduleParticipantStatus.DELETED,
                role = ScheduleParticipantRole.PARTICIPANT,
                readStatus = ScheduleParticipantReadStatus.READ,
            ),
            TestDataFactory.createScheduleParticipant(
                scheduleId = scheduleId,
                userId = friendId,
                status = ScheduleParticipantStatus.ACTIVE,
                role = ScheduleParticipantRole.OWNER,
                readStatus = ScheduleParticipantReadStatus.READ,
            ),
        )

        every { scheduleParticipantRepository.readParticipated(any(), any()) } returns listOf(scheduleParticipated)
        every { scheduleRepository.reads(any(), any(), any()) } returns scheduleInfos
        every { scheduleParticipantRepository.readsParticipants(any(), any()) } returns participants

        val (schedules, count) = scheduleService.fetches(userId, targetScheduleType)

        assert(schedules.size == 1)
        assert(schedules[0].participants.size == 1)
        assert(schedules[0].isOwned == false)
        assert(schedules[0].isParticipant == false)
        assert(count == 1)
    }

    @Test
    fun `스케줄 업데이트 - 나는 참여자가 이고 새로운 친구가 추가 되었을 떄 성공 (참여 이력 X)`() {
        val userId = TestDataFactory.createUserId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleTime = TestDataFactory.createScheduledTime()
        val scheduleContent = TestDataFactory.createScheduleContent()
        val friendId = TestDataFactory.createFriendId()
        val friendIds = listOf(friendId)

        val owner = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.ACTIVE,
            role = ScheduleParticipantRole.OWNER,
            readStatus = ScheduleParticipantReadStatus.READ,
        )

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns owner
        every { scheduleRepository.update(scheduleId, scheduleTime, scheduleContent) } just Runs
        every { scheduleParticipantRepository.readAllParticipants(scheduleId) } returns listOf(owner)
        every { scheduleParticipantRepository.removeParticipants(scheduleId, emptyList()) } just Runs
        every { scheduleParticipantRepository.updateParticipantsStatus(scheduleId, emptyList(), any()) } just Runs
        every { scheduleParticipantRepository.appendParticipants(scheduleId, friendIds) } just Runs
        every { scheduleLogRepository.appendLog(scheduleId, userId, any()) } just Runs

        assertDoesNotThrow {
            scheduleService.update(userId, scheduleId, scheduleTime, scheduleContent, friendIds, true)
        }
    }

    @Test
    fun `스케줄 업데이트 - 나는 참여자가 이고 기존의 친구가 상태가 삭제됨 이었을떄 성공(친구가 ACTIVATE 처리됨)`() {
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
            role = ScheduleParticipantRole.OWNER,
            readStatus = ScheduleParticipantReadStatus.READ,
        )
        val friendParticipants = listOf(
            TestDataFactory.createScheduleParticipant(
                userId = friendId,
                scheduleId = scheduleId,
                status = ScheduleParticipantStatus.DELETED,
                role = ScheduleParticipantRole.PARTICIPANT,
                readStatus = ScheduleParticipantReadStatus.READ,
            ),
        )
        // 참여자 목록에는 삭제된 참여자가 포함되어 있음
        val scheduleParticipants = friendParticipants.plus(userParticipant)

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns userParticipant
        every { scheduleRepository.update(scheduleId, scheduleTime, scheduleContent) } just Runs
        every { scheduleParticipantRepository.readAllParticipants(scheduleId) } returns scheduleParticipants
        every { scheduleParticipantRepository.removeParticipants(scheduleId, emptyList()) } just Runs
        every { scheduleParticipantRepository.updateParticipantsStatus(scheduleId, friendIds, any()) } just Runs
        every { scheduleParticipantRepository.appendParticipants(scheduleId, emptyList()) } just Runs
        every { scheduleLogRepository.appendLog(scheduleId, userId, any()) } just Runs

        assertDoesNotThrow {
            scheduleService.update(userId, scheduleId, scheduleTime, scheduleContent, friendIds, true)
        }
    }

    @Test
    fun `스케줄 업데이트 - 나는 참여자가 아니고 기존의 친구가 상태를 변경 할 수 있음 - (친구가 ACTIVATE 처리됨)`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleTime = TestDataFactory.createScheduledTime()
        val scheduleContent = TestDataFactory.createScheduleContent()
        val friendIds = listOf(friendId)
        val userParticipant = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.DELETED,
            role = ScheduleParticipantRole.OWNER,
            readStatus = ScheduleParticipantReadStatus.READ,
        )
        val friendParticipants = listOf(
            TestDataFactory.createScheduleParticipant(
                userId = friendId,
                scheduleId = scheduleId,
                status = ScheduleParticipantStatus.DELETED,
                role = ScheduleParticipantRole.PARTICIPANT,
                readStatus = ScheduleParticipantReadStatus.READ,
            ),
        )
        // 참여자 목록에는 삭제된 참여자가 포함되어 있음
        val scheduleParticipants = friendParticipants

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns userParticipant
        every { scheduleRepository.update(scheduleId, scheduleTime, scheduleContent) } just Runs
        every { scheduleParticipantRepository.readAllParticipants(scheduleId) } returns scheduleParticipants
        every { scheduleParticipantRepository.removeParticipants(scheduleId, emptyList()) } just Runs
        every { scheduleParticipantRepository.updateParticipantsStatus(scheduleId, friendIds, any()) } just Runs
        every { scheduleParticipantRepository.appendParticipants(scheduleId, emptyList()) } just Runs
        every { scheduleLogRepository.appendLog(scheduleId, userId, any()) } just Runs

        assertDoesNotThrow {
            scheduleService.update(userId, scheduleId, scheduleTime, scheduleContent, friendIds, false)
        }
    }

    @Test
    fun `스케줄 업데이트 - 나는 참여자가 아니고 기존의 친구가 상태를 변경 할 수 있음 - (새로운 친구 추가)`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleTime = TestDataFactory.createScheduledTime()
        val scheduleContent = TestDataFactory.createScheduleContent()
        val friendIds = listOf(friendId)
        val userParticipant = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.DELETED,
            role = ScheduleParticipantRole.OWNER,
            readStatus = ScheduleParticipantReadStatus.READ,
        )

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns userParticipant
        every { scheduleRepository.update(scheduleId, scheduleTime, scheduleContent) } just Runs
        every { scheduleParticipantRepository.readAllParticipants(scheduleId) } returns listOf()
        every { scheduleParticipantRepository.removeParticipants(scheduleId, emptyList()) } just Runs
        every { scheduleParticipantRepository.updateParticipantsStatus(scheduleId, emptyList(), any()) } just Runs
        every { scheduleParticipantRepository.appendParticipants(scheduleId, friendIds) } just Runs
        every { scheduleLogRepository.appendLog(scheduleId, userId, any()) } just Runs

        assertDoesNotThrow {
            scheduleService.update(userId, scheduleId, scheduleTime, scheduleContent, friendIds, false)
        }
    }

    @Test
    fun `스케줄 업데이트 - 나는 참여자가 아니고 기존의 친구가 상태를 변경 할 수 있음 - (기존 친구 삭제)`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleTime = TestDataFactory.createScheduledTime()
        val scheduleContent = TestDataFactory.createScheduleContent()
        val friendIds = listOf(friendId)
        val userParticipant = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.DELETED,
            role = ScheduleParticipantRole.OWNER,
            readStatus = ScheduleParticipantReadStatus.READ,
        )
        val friendParticipants = listOf(
            TestDataFactory.createScheduleParticipant(
                userId = friendId,
                scheduleId = scheduleId,
                status = ScheduleParticipantStatus.ACTIVE,
                role = ScheduleParticipantRole.PARTICIPANT,
                readStatus = ScheduleParticipantReadStatus.READ,
            ),
        )

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns userParticipant
        every { scheduleRepository.update(scheduleId, scheduleTime, scheduleContent) } just Runs
        every { scheduleParticipantRepository.readAllParticipants(scheduleId) } returns friendParticipants
        every { scheduleParticipantRepository.removeParticipants(scheduleId, friendIds) } just Runs
        every { scheduleParticipantRepository.updateParticipantsStatus(scheduleId, emptyList(), any()) } just Runs
        every { scheduleParticipantRepository.appendParticipants(scheduleId, emptyList()) } just Runs
        every { scheduleLogRepository.appendLog(scheduleId, userId, any()) } just Runs

        assertDoesNotThrow {
            scheduleService.update(userId, scheduleId, scheduleTime, scheduleContent, listOf(), false)
        }
    }

    @Test
    fun `스케줄 업데이트 실패- 나는 참여한 이력이 없음`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleTime = TestDataFactory.createScheduledTime()
        val scheduleContent = TestDataFactory.createScheduleContent()
        val friendIds = listOf(friendId)

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns null

        val result = assertThrows<NotFoundException> {
            scheduleService.update(userId, scheduleId, scheduleTime, scheduleContent, friendIds, false)
        }

        assert(result.errorCode == ErrorCode.SCHEDULE_NOT_PARTICIPANT)
    }

    @Test
    fun `스케줄 업데이트 - 낙관적 락 백오프`() {
        val userId = TestDataFactory.createUserId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleTime = TestDataFactory.createScheduledTime()
        val scheduleContent = TestDataFactory.createScheduleContent()
        val friendId = TestDataFactory.createFriendId()
        val friendIds = listOf(friendId)

        val owner = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.ACTIVE,
            role = ScheduleParticipantRole.OWNER,
            readStatus = ScheduleParticipantReadStatus.READ,
        )

        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns owner
        every { scheduleRepository.update(scheduleId, scheduleTime, scheduleContent) } throws OptimisticLockingFailureException("")

        val exception = assertThrows<ConflictException> {
            scheduleService.update(userId, scheduleId, scheduleTime, scheduleContent, friendIds, true)
        }

        assert(exception.errorCode == ErrorCode.SCHEDULE_UPDATE_FAILED)

        verify(exactly = 5) { scheduleRepository.update(scheduleId, scheduleTime, scheduleContent) }
    }

    @Test
    fun `스케줄 취소 성공`() {
        val userId = TestDataFactory.createUserId()
        val scheduleId = TestDataFactory.createScheduleId()
        val userParticipant = TestDataFactory.createScheduleParticipant(
            userId = userId,
            scheduleId = scheduleId,
            status = ScheduleParticipantStatus.ACTIVE,
            role = ScheduleParticipantRole.OWNER,
            readStatus = ScheduleParticipantReadStatus.READ,
        )
        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns userParticipant
        every { scheduleParticipantRepository.removeParticipant(scheduleId, userId) } just Runs
        every { scheduleLogRepository.appendLog(scheduleId, userId, any()) } just Runs

        assertDoesNotThrow {
            scheduleService.cancel(userId, scheduleId)
        }
    }

    @Test
    fun `스케줄 취소 실패 - 참여한 이력이 없음`() {
        val userId = TestDataFactory.createUserId()
        val scheduleId = TestDataFactory.createScheduleId()
        every { scheduleParticipantRepository.readParticipant(userId, scheduleId) } returns null

        val result = assertThrows<NotFoundException> {
            scheduleService.cancel(userId, scheduleId)
        }

        assert(result.errorCode == ErrorCode.SCHEDULE_NOT_PARTICIPANT)
    }

    @Test
    fun `스케줄 가져오기 성공 - 본인 소유이자 참여자임`() {
        val userId = TestDataFactory.createUserId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleInfo = TestDataFactory.createScheduleInfo(scheduleId, ScheduleStatus.ACTIVE)
        val participants = listOf(
            TestDataFactory.createScheduleParticipant(
                userId = userId,
                scheduleId = scheduleId,
                status = ScheduleParticipantStatus.ACTIVE,
                role = ScheduleParticipantRole.OWNER,
                readStatus = ScheduleParticipantReadStatus.READ,
            ),
        )

        every { scheduleRepository.read(scheduleId, ScheduleStatus.ACTIVE) } returns scheduleInfo
        every {
            scheduleParticipantRepository.readParticipants(
                scheduleId,
                ScheduleParticipantStatus.ACTIVE,
            )
        } returns participants
        every {
            scheduleParticipantRepository.updateParticipantReadStatus(
                userId, scheduleId,
                ScheduleParticipantReadStatus.READ,
            )
        } just Runs

        val result = scheduleService.fetch(userId, scheduleId)

        assert(result.info.scheduleId == scheduleId)
        assert(result.isOwned)
        assert(result.isParticipant)
        assert(result.participants.isEmpty())
    }

    @Test
    fun `스케줄 가져오기 성공 - 본인 소유가 아니고 참여자가 아님`() {
        val userId = TestDataFactory.createUserId()
        val scheduleId = TestDataFactory.createScheduleId()
        val scheduleInfo = TestDataFactory.createScheduleInfo(scheduleId, ScheduleStatus.ACTIVE)
        val participants = listOf(
            TestDataFactory.createScheduleParticipant(
                userId = userId,
                scheduleId = scheduleId,
                status = ScheduleParticipantStatus.DELETED,
                role = ScheduleParticipantRole.PARTICIPANT,
                readStatus = ScheduleParticipantReadStatus.READ,
            ),
        )

        every { scheduleRepository.read(scheduleId, ScheduleStatus.ACTIVE) } returns scheduleInfo
        every {
            scheduleParticipantRepository.readParticipants(
                scheduleId,
                ScheduleParticipantStatus.ACTIVE,
            )
        } returns participants
        every {
            scheduleParticipantRepository.updateParticipantReadStatus(
                userId, scheduleId,
                ScheduleParticipantReadStatus.READ,
            )
        } just Runs

        val result = scheduleService.fetch(userId, scheduleId)

        assert(result.info.scheduleId == scheduleId)
        assert(!result.isOwned)
        assert(!result.isParticipant)
        assert(result.participants.isEmpty())
    }

    @Test
    fun `스케줄 가져오기 실패 - 존재 하지 않음`() {
        val userId = TestDataFactory.createUserId()
        val scheduleId = TestDataFactory.createScheduleId()

        every { scheduleRepository.read(scheduleId, ScheduleStatus.ACTIVE) } returns null

        val result = assertThrows<NotFoundException> {
            scheduleService.fetch(userId, scheduleId)
        }

        assert(result.errorCode == ErrorCode.SCHEDULE_NOT_FOUND)
    }

    @Test
    fun `일정 로그 목록 가져오기`() {
        val userId = TestDataFactory.createUserId()
        val scheduleLog = TestDataFactory.createScheduleLog()
        val scheduleParticipated = TestDataFactory.createScheduleParticipant(
            userId,
            scheduleLog.scheduleId,
            ScheduleParticipantStatus.ACTIVE,
            ScheduleParticipantRole.OWNER,
            ScheduleParticipantReadStatus.READ,
        )
        val participants = listOf(scheduleParticipated)

        every {
            scheduleParticipantRepository.readParticipated(
                userId,
                ScheduleParticipantStatus.ACTIVE,
            )
        } returns participants
        every { scheduleLogRepository.readsLogs(participants.map { it.scheduleId }) } returns listOf(scheduleLog)

        val result = scheduleService.fetchLogs(userId)

        assert(result.size == 1)
    }
}
