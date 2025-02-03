package org.chewing.v1.facade

import org.chewing.v1.service.chat.DirectChatRoomService
import org.springframework.stereotype.Service

@Service
class DirectChatRoomFacade(
    private val chatRoomService: DirectChatRoomService,
)
