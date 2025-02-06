package org.chewing.v1.jparepository.chat

import org.chewing.v1.jpaentity.chat.DirectChatRoomJpaEntity
import org.chewing.v1.jpaentity.chat.GroupChatRoomJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface GroupChatRoomJpaRepository :JpaRepository<GroupChatRoomJpaEntity, String> {
}
