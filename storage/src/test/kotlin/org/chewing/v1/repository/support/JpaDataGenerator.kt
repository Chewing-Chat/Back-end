package org.chewing.v1.repository.support

import org.chewing.v1.jpaentity.announcement.AnnouncementJpaEntity
import org.chewing.v1.jpaentity.auth.LoggedInJpaEntity
import org.chewing.v1.jpaentity.chat.ChatRoomJpaEntity
import org.chewing.v1.jpaentity.chat.GroupChatRoomMemberJpaEntity
import org.chewing.v1.jpaentity.chat.PersonalChatRoomMemberJpaEntity
import org.chewing.v1.jpaentity.emoticon.EmoticonJpaEntity
import org.chewing.v1.jpaentity.emoticon.EmoticonPackJpaEntity
import org.chewing.v1.jpaentity.feed.FeedDetailJpaEntity
import org.chewing.v1.jpaentity.feed.FeedJpaEntity
import org.chewing.v1.jpaentity.feed.FeedVisibilityEntity
import org.chewing.v1.jpaentity.feed.FeedVisibilityId
import org.chewing.v1.jpaentity.friend.FriendShipJpaEntity
import org.chewing.v1.jpaentity.user.*
import org.chewing.v1.jpaentity.user.PushNotificationJpaEntity
import org.chewing.v1.jpaentity.user.ScheduleJpaEntity
import org.chewing.v1.jpaentity.user.UserEmoticonJpaEntity
import org.chewing.v1.jpaentity.user.UserJpaEntity
import org.chewing.v1.jparepository.announcement.AnnouncementJpaRepository
import org.chewing.v1.jparepository.auth.LoggedInJpaRepository
import org.chewing.v1.jparepository.chat.ChatRoomJpaRepository
import org.chewing.v1.jparepository.chat.GroupChatRoomMemberJpaRepository
import org.chewing.v1.jparepository.chat.PersonalChatRoomMemberJpaRepository
import org.chewing.v1.jparepository.emoticon.EmoticonJpaRepository
import org.chewing.v1.jparepository.emoticon.EmoticonPackJpaRepository
import org.chewing.v1.jparepository.feed.FeedDetailJpaRepository
import org.chewing.v1.jparepository.feed.FeedJpaRepository
import org.chewing.v1.jparepository.feed.FeedVisibilityJpaRepository
import org.chewing.v1.jparepository.friend.FriendShipJpaRepository
import org.chewing.v1.jparepository.user.*
import org.chewing.v1.jparepository.user.PushNotificationJpaRepository
import org.chewing.v1.jparepository.user.ScheduleJpaRepository
import org.chewing.v1.jparepository.user.UserJpaRepository
import org.chewing.v1.model.announcement.Announcement
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.chat.room.ChatRoomInfo
import org.chewing.v1.model.emoticon.EmoticonInfo
import org.chewing.v1.model.emoticon.EmoticonPackInfo
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.schedule.Schedule
import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleTime
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserEmoticonPackInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class JpaDataGenerator {

    @Autowired
    private lateinit var loggedInJpaRepository: LoggedInJpaRepository

    @Autowired
    private lateinit var announcementJpaRepository: AnnouncementJpaRepository

    @Autowired
    private lateinit var scheduleJpaRepository: ScheduleJpaRepository

    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    private lateinit var pushNotificationJpaRepository: PushNotificationJpaRepository

    @Autowired
    private lateinit var feedJpaRepository: FeedJpaRepository

    @Autowired
    private lateinit var feedDetailJpaRepository: FeedDetailJpaRepository

    @Autowired
    private lateinit var friendShipJpaRepository: FriendShipJpaRepository

    @Autowired
    private lateinit var userEmoticonJpaRepository: UserEmoticonJpaRepository

    @Autowired
    private lateinit var emoticonPackJpaRepository: EmoticonPackJpaRepository

    @Autowired
    private lateinit var emoticonJpaRepository: EmoticonJpaRepository

    @Autowired
    private lateinit var chatRoomJpaRepository: ChatRoomJpaRepository

    @Autowired
    private lateinit var groupChatRoomMemberJpaRepository: GroupChatRoomMemberJpaRepository

    @Autowired
    private lateinit var personalChatRoomMemberJpaRepository: PersonalChatRoomMemberJpaRepository

    @Autowired
    private lateinit var feedVisibilityJpaRepository: FeedVisibilityJpaRepository

    fun userEntityData(credential: PhoneNumber, userName: String, access: AccessStatus): User {
        val user = UserJpaEntity.generate(credential, userName, access)
        userJpaRepository.save(user)
        return user.toUser()
    }

    fun loggedInEntityData(refreshToken: RefreshToken, userId: String) {
        val loggedIn = LoggedInJpaEntity.generate(refreshToken, userId)
        loggedInJpaRepository.save(loggedIn)
    }

    fun announcementEntityData(): Announcement {
        val announcement = AnnouncementJpaEntity.generate("title", "content")
        announcementJpaRepository.save(announcement)
        return announcement.toAnnouncement()
    }

    fun announcementEntityDataList() {
        (1..10).map {
            AnnouncementJpaEntity.generate("title $it", "content $it")
        }.forEach { announcement ->
            announcementJpaRepository.save(announcement)
        }
    }

    fun scheduleEntityData(content: ScheduleContent, time: ScheduleTime, userId: String): Schedule {
        val scheduleEntity = ScheduleJpaEntity.generate(content, time, userId)
        scheduleJpaRepository.save(scheduleEntity)
        return scheduleEntity.toSchedule()
    }

    fun feedVisibilityEntityData(feedId: String, userId: String) {
        feedVisibilityJpaRepository.save(FeedVisibilityEntity(FeedVisibilityId(feedId, userId)))
    }

    fun feedVisibilityEntityDataList(feedId: String, userIds: List<String>) {
        feedVisibilityJpaRepository.saveAll(userIds.map { FeedVisibilityEntity(FeedVisibilityId(feedId, it)) })
    }

    fun pushNotificationData(userId: String): PushToken {
        val user = UserProvider.buildNormal(userId)
        val device = PushTokenProvider.buildDeviceNormal()
        val appToken = PushTokenProvider.buildAppTokenNormal()
        return pushNotificationJpaRepository.save(
            PushNotificationJpaEntity.generate(
                appToken,
                device,
                user,
            ),
        ).toPushToken()
    }

    fun feedEntityData(userId: String): FeedInfo =
        feedJpaRepository.save(FeedJpaEntity.generate("content", userId)).toFeedInfo()

    fun feedEntityDataList(userId: String): List<FeedInfo> {
        val feedJpaEntityList = (1..10).map {
            FeedJpaEntity.generate("content $it", userId)
        }
        feedJpaRepository.saveAll(feedJpaEntityList)
        return feedJpaEntityList.map { it.toFeedInfo() }
    }

    fun feedDetailEntityDataAsc(feedId: String): List<FeedDetail> {
        val medias = MediaProvider.buildFeedContents()
        val feedEntities = FeedDetailJpaEntity.generate(medias, feedId)
        feedDetailJpaRepository.saveAll(feedEntities)
        return feedEntities.map { it.toFeedDetail() }
    }

    fun friendShipEntityData(userId: String, friendId: String, access: AccessStatus) {
        val friendName = UserProvider.buildFriendName()
        val entity = FriendShipJpaEntity.generate(userId, friendId, friendName)
        when (access) {
            AccessStatus.DELETE -> entity.updateDelete()
            AccessStatus.BLOCK -> entity.updateBlock()
            AccessStatus.BLOCKED -> entity.updateBlocked()
            else -> {}
        }
        friendShipJpaRepository.save(entity)
    }

    fun userEmoticonEntityData(userId: String, emoticonPackId: String): UserEmoticonPackInfo {
        val emoticon = UserEmoticonJpaEntity(UserEmoticonId(userId, emoticonPackId), LocalDateTime.now())
        userEmoticonJpaRepository.save(emoticon)
        return emoticon.toUserEmoticon()
    }

    fun emoticonPackEntityData(): EmoticonPackInfo {
        val emoticonPack = EmoticonPackJpaEntity.of("emoticonPackImageUrl", "emoticonPackName")
        emoticonPackJpaRepository.save(emoticonPack)
        return emoticonPack.toEmoticonPack()
    }

    fun emoticonEntityData(emoticonPackId: String): EmoticonInfo {
        val emoticon = EmoticonJpaEntity.of("emoticonImageUrl", "emoticonName", emoticonPackId)
        emoticonJpaRepository.save(emoticon)
        return emoticon.toEmoticon()
    }

    fun emoticonEntityDataList(emoticonPackId: String): List<EmoticonInfo> {
        val emoticonList = (1..10).map {
            EmoticonJpaEntity.of("emoticonImageUrl $it", "emoticonName $it", emoticonPackId)
        }
        emoticonJpaRepository.saveAll(emoticonList)
        return emoticonList.map { it.toEmoticon() }
    }

    fun chatRoomEntityData(isGroup: Boolean): ChatRoomInfo {
        val chatRoom = ChatRoomJpaEntity.generate(isGroup)
        chatRoomJpaRepository.save(chatRoom)
        return chatRoom.toChatRoomInfo()
    }

    fun groupChatRoomMemberEntityData(chatRoomId: String, userId: String, number: ChatNumber) {
        groupChatRoomMemberJpaRepository.save(GroupChatRoomMemberJpaEntity.generate(userId, chatRoomId, number))
    }

    fun groupChatRoomMemberEntityDataList(chatRoomId: String, userIds: List<String>, number: ChatNumber) {
        groupChatRoomMemberJpaRepository.saveAll(
            userIds.map {
                GroupChatRoomMemberJpaEntity.generate(
                    it,
                    chatRoomId,
                    number,
                )
            },
        )
    }

    fun personalChatRoomMemberEntityData(userId: String, friendId: String, chatRoomId: String, number: ChatNumber) {
        personalChatRoomMemberJpaRepository.save(
            PersonalChatRoomMemberJpaEntity.generate(
                userId,
                friendId,
                chatRoomId,
                number,
            ),
        )
    }
}
