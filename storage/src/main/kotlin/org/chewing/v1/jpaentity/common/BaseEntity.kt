package org.chewing.v1.jpaentity.common

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
internal abstract class BaseEntity {
    @Column(name = "created_at", columnDefinition = "datetime(6)", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @LastModifiedDate
    @Column(name = "modified_at", columnDefinition = "datetime(6)")
    var modifiedAt: LocalDateTime? = null
        protected set
}
