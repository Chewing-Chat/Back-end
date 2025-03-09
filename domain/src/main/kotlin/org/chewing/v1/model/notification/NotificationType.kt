package org.chewing.v1.model.notification

enum class NotificationType {
    GROUP_CHAT_NORMAL,
    GROUP_CHAT_FILE,
    GROUP_CHAT_LEAVE,
    GROUP_CHAT_REPLY,
    GROUP_CHAT_INVITE,
    DIRECT_CHAT_NORMAL,
    DIRECT_CHAT_FILE,
    DIRECT_CHAT_REPLY,
    SCHEDULE_CREATE,
    SCHEDULE_UPDATE,
    SCHEDULE_DELETE,
    SCHEDULE_CANCEL,
    ;

    fun toLowerCase(): String {
        return this.name.lowercase()
    }
}
