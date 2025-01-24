package org.chewing.v1.jparepository.emoticon

import org.chewing.v1.jpaentity.emoticon.EmoticonJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface EmoticonJpaRepository : JpaRepository<EmoticonJpaEntity, String> {
    fun findAllByEmoticonPackIdIn(emoticonPackIds: List<String>): List<EmoticonJpaEntity>
}
