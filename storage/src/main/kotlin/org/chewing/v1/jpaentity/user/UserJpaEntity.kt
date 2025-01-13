package org.chewing.v1.jpaentity.user

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.hibernate.annotations.DynamicInsert
import java.util.*

@DynamicInsert
@Entity
@Table(name = "`user`", schema = "chewing")
internal class UserJpaEntity(
    @Id
    private val userId: String = UUID.randomUUID().toString(),

    private var pictureUrl: String,

    @Enumerated(EnumType.STRING)
    private var pictureType: MediaType,

    private var backgroundPictureUrl: String,

    @Enumerated(EnumType.STRING)
    private var backgroundPictureType: MediaType,

    private var birth: String,

    private var countryCode: String,

    private var phoneNumber: String,

    private var password: String,

    @Enumerated(EnumType.STRING)
    private var type: AccessStatus,

    private var name: String,
) : BaseEntity() {
    companion object {
        fun generate(phoneNumber: PhoneNumber, userName: String, access: AccessStatus): UserJpaEntity {
            return UserJpaEntity(
                pictureUrl = "",
                pictureType = MediaType.IMAGE_BASIC,
                backgroundPictureUrl = "",
                backgroundPictureType = MediaType.IMAGE_BASIC,
                birth = "",
                countryCode = phoneNumber.countryCode,
                phoneNumber = phoneNumber.number,
                type = access,
                name = userName,
                password = "",
            )
        }
    }

    fun toUser(): User {
        return User.of(
            this.userId,
            this.name,
            this.birth,
            Media.of(FileCategory.PROFILE, this.pictureUrl, 0, this.pictureType),
            Media.of(FileCategory.BACKGROUND, this.backgroundPictureUrl, 0, this.backgroundPictureType),
            this.type,
            PhoneNumber.of(this.countryCode, this.phoneNumber),
            this.password,
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

    fun updateUserName(userName: String) {
        this.name = userName
    }

    fun updateBirth(birth: String) {
        this.birth = birth
    }

    fun updateDelete() {
        this.type = AccessStatus.DELETE
    }

    fun updatePassword(password: String) {
        this.password = password
        this.type = AccessStatus.ACCESS
    }

    fun updateAccess() {
        this.type = AccessStatus.ACCESS
    }
}
