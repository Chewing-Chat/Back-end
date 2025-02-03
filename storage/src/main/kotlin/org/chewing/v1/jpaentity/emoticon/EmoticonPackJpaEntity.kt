package org.chewing.v1.jpaentity.emoticon

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.emoticon.EmoticonPackInfo
import org.hibernate.annotations.DynamicInsert
import java.util.*

@DynamicInsert
@Entity
@Table(name = "emoticon_pack", schema = "chewing")
internal class EmoticonPackJpaEntity(
    @Id
    private val emoticonPackId: String = UUID.randomUUID().toString(),

    private val packUrl: String,

    private val packName: String,
) : BaseEntity() {
    companion object {
        fun of(
            packUrl: String,
            packName: String,
        ): EmoticonPackJpaEntity {
            return EmoticonPackJpaEntity(
                packUrl = packUrl,
                packName = packName,
            )
        }
    }
    fun toEmoticonPack(): EmoticonPackInfo {
        return EmoticonPackInfo.of(
            emoticonPackId,
            packName,
            packUrl,
        )
    }

    fun getEmoticonId(): String {
        return emoticonPackId
    }
}
