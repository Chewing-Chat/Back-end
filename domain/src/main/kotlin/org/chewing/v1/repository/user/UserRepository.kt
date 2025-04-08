package org.chewing.v1.repository.user

import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface UserRepository {
    fun read(userId: UserId, status: AccessStatus): UserInfo?
    fun reads(userIds: List<UserId>, status: AccessStatus): List<UserInfo>
    fun remove(userId: UserId): UserInfo?
    fun updateMedia(userId: UserId, media: Media): Media?
    fun append(contact: Contact, userName: String): UserInfo
    fun updatePassword(userId: UserId, password: String): UserId?
    fun updateStatusMessage(userId: UserId, statusMessage: String): UserId?
    fun updateBirthday(userId: UserId, birthday: LocalDate): UserId?
    fun readByContact(contact: Contact, accessStatus: AccessStatus): UserInfo?
    fun readsByContacts(contacts: List<Contact>, accessStatus: AccessStatus): List<UserInfo>
    fun appendPassword(userId: UserId, password: String)
}
