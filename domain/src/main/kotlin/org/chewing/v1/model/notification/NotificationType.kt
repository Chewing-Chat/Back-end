package org.chewing.v1.model.notification

enum class NotificationType {
    CHAT_NORMAL,
    CHAT_FILE,
    CHAT_LEAVE,
    CHAT_REPLY,
    CHAT_INVITE,
    ;

    fun toLowerCase(): String {
        return this.name.lowercase()
    }
}
