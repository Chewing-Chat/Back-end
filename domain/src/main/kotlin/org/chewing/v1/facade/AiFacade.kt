package org.chewing.v1.facade

import org.chewing.v1.model.ai.DateTarget
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.service.ai.AiService
import org.chewing.v1.service.chat.ChatLogService
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.ScheduleService
import org.springframework.stereotype.Component

@Component
class AiFacade(
    private val feedService: FeedService,
    private val aiService: AiService,
    private val friendShipService: FriendShipService,
    private val chatLogService: ChatLogService,
    private val scheduleService: ScheduleService,
) {
    fun getAiRecentSummary(userId: String, friendId: String, targetDate: DateTarget): String {
        val friendName = friendShipService.getFriendName(userId, friendId)
        val feeds = feedService.getFriendFulledFeeds(friendId, targetDate)
        return aiService.getAiRecentSummary(friendName, feeds)
    }

    fun getAiSearchChat(chatRoomId: String, prompt: String): List<ChatLog> {
        val resultKeyword = aiService.getAiSearchChat(prompt)
        return chatLogService.getChatKeyWordLog(chatRoomId, resultKeyword)
    }

    fun createAiSchedule(userId: String, prompt: String): String {
        val promptedSchedule = aiService.getAiSchedule(prompt)
        return scheduleService.createAiSchedule(userId, promptedSchedule)
    }
}
