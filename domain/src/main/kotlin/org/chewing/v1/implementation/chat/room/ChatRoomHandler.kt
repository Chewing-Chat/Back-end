package org.chewing.v1.implementation.chat.room

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.chat.room.ChatNumber
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Component

@Component
class ChatRoomHandler(
    private val chatRoomUpdater: ChatRoomUpdater,
) {
    fun lockFavoriteChatRoom(chatRoomId: String, userId: String, isFavorite: Boolean, isGroup: Boolean) {
        var retryCount = 0
        val maxRetry = 5
        var delayTime = 100L
        while (retryCount < maxRetry) {
            try {
                chatRoomUpdater.updateFavorite(chatRoomId, userId, isFavorite, isGroup)
                return
            } catch (ex: OptimisticLockingFailureException) {
                // 예외 처리: 버전 충돌 시 재시도
                retryCount++
                Thread.sleep(delayTime)
                delayTime *= 2
            }
        }
        throw ConflictException(ErrorCode.CHATROOM_FAVORITE_FAILED)
    }

    fun lockReadChatRoom(userId: String, number: ChatNumber, isGroup: Boolean) {
        var retryCount = 0
        val maxRetry = 5
        var delayTime = 100L
        while (retryCount < maxRetry) {
            try {
                chatRoomUpdater.updateRead(userId, number, isGroup)
                return
            } catch (ex: OptimisticLockingFailureException) {
                // 예외 처리: 버전 충돌 시 재시도
                retryCount++
                Thread.sleep(delayTime)
                delayTime *= 2
            }
        }
    }
}
