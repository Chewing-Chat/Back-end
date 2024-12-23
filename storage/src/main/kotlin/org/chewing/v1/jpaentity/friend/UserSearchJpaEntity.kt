package org.chewing.v1.jpaentity.friend

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.friend.UserSearch
import org.hibernate.annotations.DynamicInsert
import java.util.UUID

@Entity
@DynamicInsert
@Table(name = "friend_search", schema = "chewing")
internal class UserSearchJpaEntity(
    @Id
    private val searchId: String = UUID.randomUUID().toString(),
    private val searchText: String,
    private val userId: String,
) : BaseEntity() {
    fun toFriendSearch(): UserSearch {
        return UserSearch.of(
            keyword = searchText,
            searchTime = createdAt,
        )
    }

    companion object {
        fun fromFriendSearch(userId: String, keyword: String): UserSearchJpaEntity {
            return UserSearchJpaEntity(
                searchText = keyword,
                userId = userId,
            )
        }
    }
}
