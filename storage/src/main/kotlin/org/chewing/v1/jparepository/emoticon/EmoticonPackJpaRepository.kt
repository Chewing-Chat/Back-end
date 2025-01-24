package org.chewing.v1.jparepository.emoticon

import org.chewing.v1.jpaentity.emoticon.EmoticonPackJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

internal interface EmoticonPackJpaRepository : JpaRepository<EmoticonPackJpaEntity, String>
