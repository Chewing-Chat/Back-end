package org.chewing.v1.model.notification

enum class NotificationType {
    COMMENT,
    LIKE,
    CHAT_NORMAL,
    CHAT_FILE,
    CHAT_LEAVE,
    CHAT_REPLY,
    CHAT_INVITE,
    CHAT_BOMB,
    ;

    fun toLowerCase(): String {
        return this.name.lowercase()
    }
}
