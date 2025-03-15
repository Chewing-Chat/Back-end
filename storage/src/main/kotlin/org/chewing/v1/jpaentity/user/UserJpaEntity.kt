package org.chewing.v1.jpaentity.user

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.contact.PhoneNumber
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert
import java.util.*

@DynamicInsert
@Entity
@Table(
    name = "`user`",
    schema = "chewing",
    indexes = [
        Index(name = "user_idx_phone_status", columnList = "phoneNumber, status"),
        Index(name = "user_idx_userid_status", columnList = "userId, status"),
    ],
)
internal class UserJpaEntity(
    @Id
    private val userId: String = UUID.randomUUID().toString(),

    private var pictureUrl: String,

    @Enumerated(EnumType.STRING)
    private var pictureType: MediaType,

    private var backgroundPictureUrl: String,

    @Enumerated(EnumType.STRING)
    private var backgroundPictureType: MediaType,

    private var phoneNumber: String,

    private var password: String,

    @Enumerated(EnumType.STRING)
    private var status: AccessStatus,

    private var name: String,

    private var statusMessage: String,
) : BaseEntity() {
    companion object {
        fun generate(phoneNumber: PhoneNumber, userName: String, access: AccessStatus): UserJpaEntity {
            return UserJpaEntity(
                pictureUrl = "",
                pictureType = MediaType.IMAGE_BASIC,
                backgroundPictureUrl = "",
                backgroundPictureType = MediaType.IMAGE_BASIC,
                phoneNumber = phoneNumber.e164PhoneNumber,
                status = access,
                name = userName,
                password = "",
                statusMessage = "",
            )
        }
    }

    fun toUser(): UserInfo {
        return UserInfo.of(
            UserId.of(this.userId),
            this.name,
            Media.of(FileCategory.PROFILE, this.pictureUrl, 0, this.pictureType),
            this.status,
            PhoneNumber.of(this.phoneNumber),
            this.password,
            this.statusMessage,
        )
    }

    fun updateUserPictureUrl(media: Media) {
        this.pictureUrl = media.url
        this.pictureType = media.type
    }

    fun updateBackgroundPictureUrl(media: Media) {
        this.backgroundPictureUrl = media.url
        this.backgroundPictureType = media.type
    }

    fun updateStatusMessage(statusMessage: String) {
        this.statusMessage = statusMessage
    }

    fun updatePassword(password: String) {
        this.password = password
        this.status = AccessStatus.ACCESS
    }

    fun updateAccessStatus(accessStatus: AccessStatus) {
        this.status = accessStatus
    }
    fun toUserId(): UserId {
        return UserId.of(this.userId)
    }

    fun updateUserName(name: String) {
        this.name = name
    }
}
