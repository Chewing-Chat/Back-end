package org.chewing.v1.jpaentity.user

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.contact.Email
import org.chewing.v1.model.contact.Phone
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.media.MediaType
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserAccount
import org.chewing.v1.model.user.UserName
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

    private var firstName: String,

    private var lastName: String,

    private var birth: String,

    private var emailId: String?,

    private var phoneNumberId: String?,
    private var ttsUrl: String?,
    @Enumerated(EnumType.STRING)
    private var ttsType: MediaType,

    @Enumerated(EnumType.STRING)
    private var type: AccessStatus,
) : BaseEntity() {
    companion object {
        fun generateByEmail(email: Email): UserJpaEntity {
            return UserJpaEntity(
                firstName = "",
                lastName = "",
                birth = "",
                pictureUrl = "",
                backgroundPictureUrl = "",
                type = AccessStatus.NOT_ACCESS,
                emailId = email.emailId,
                phoneNumberId = null,
                pictureType = MediaType.IMAGE_BASIC,
                backgroundPictureType = MediaType.IMAGE_BASIC,
                ttsUrl = null,
                ttsType = MediaType.VIDEO_BASIC,
            )
        }

        fun generateByPhone(phone: Phone): UserJpaEntity {
            return UserJpaEntity(
                firstName = "",
                lastName = "",
                birth = "",
                pictureUrl = "",
                backgroundPictureUrl = "",
                type = AccessStatus.NOT_ACCESS,
                emailId = null,
                phoneNumberId = phone.phoneId,
                pictureType = MediaType.IMAGE_BASIC,
                backgroundPictureType = MediaType.IMAGE_BASIC,
                ttsUrl = null,
                ttsType = MediaType.VIDEO_BASIC,
            )
        }
    }

    fun toUser(): User {
        return User.of(
            this.userId,
            this.firstName,
            this.lastName,
            this.birth,
            Media.of(FileCategory.PROFILE, this.pictureUrl, 0, this.pictureType),
            Media.of(FileCategory.BACKGROUND, this.backgroundPictureUrl, 0, this.backgroundPictureType),
            this.type,
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

    fun updateUserName(userName: UserName) {
        this.firstName = userName.firstName
        this.lastName = userName.lastName
    }

    fun updateBirth(birth: String) {
        this.birth = birth
    }

    fun updateDelete() {
        this.type = AccessStatus.DELETE
    }

    fun updateAccess() {
        this.type = AccessStatus.ACCESS
    }

    fun updateContact(contact: Contact) {
        when (contact) {
            is Email -> this.emailId = contact.emailId
            is Phone -> this.phoneNumberId = contact.phoneId
        }
    }

    fun updateTTS(tts: Media) {
        this.ttsUrl = tts.url
        this.ttsType = tts.type
    }

    fun toTTS(): Media {
        return Media.of(FileCategory.TTS, this.ttsUrl ?: "", 0, this.ttsType)
    }

    fun toUserAccount(): UserAccount {
        return UserAccount.of(
            this.toUser(),
            this.emailId,
            this.phoneNumberId,
        )
    }
}
