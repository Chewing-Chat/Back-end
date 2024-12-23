package org.chewing.v1.repository.user

import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserAccount
import org.chewing.v1.model.user.UserContent
import org.chewing.v1.model.user.UserName
import org.springframework.stereotype.Repository

@Repository
interface UserRepository {
    fun read(userId: String): User?
    fun readAccount(userId: String): UserAccount?
    fun reads(userIds: List<String>): List<User>
    fun readByContact(contact: Contact): User?
    fun remove(userId: String): User?
    fun updateMedia(userId: String, media: Media): Media?
    fun updateName(userId: String, userName: UserName): String?
    fun updateContact(userId: String, contact: Contact): String?
    fun updateAccess(userId: String, userContent: UserContent): String?
    fun append(contact: Contact): User
    fun checkContactIsUsedByElse(contact: Contact, userId: String): Boolean
    fun updateBirth(userId: String, birth: String): String?
}
