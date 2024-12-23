package org.chewing.v1.implementation.auth

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.contact.Email
import org.chewing.v1.model.contact.Phone
import org.chewing.v1.repository.auth.EmailRepository
import org.chewing.v1.repository.auth.PhoneRepository
import org.chewing.v1.repository.user.UserRepository
import org.springframework.stereotype.Component

@Component
class AuthValidator(
    private val userRepository: UserRepository,
    private val phoneRepository: PhoneRepository,
    private val emailRepository: EmailRepository,
) {
    fun validateVerifyCode(contact: Contact, verificationCode: String) {
        if (!contact.validationCode.validateCode(verificationCode)) {
            throw ConflictException(ErrorCode.WRONG_VALIDATE_CODE)
        }
        if (contact.validationCode.validateExpired()) {
            throw ConflictException(ErrorCode.EXPIRED_VALIDATE_CODE)
        }
    }

    fun validateContactIsUsed(targetContact: Credential, userId: String) {
        val contact = when (targetContact) {
            is EmailAddress -> emailRepository.read(targetContact)
            is PhoneNumber -> phoneRepository.read(targetContact)
        }

        contact?.let {
            if (userRepository.checkContactIsUsedByElse(it, userId)) {
                val errorCode = when (it) {
                    is Email -> ErrorCode.EMAIL_ADDRESS_IS_USED
                    is Phone -> ErrorCode.PHONE_NUMBER_IS_USED
                }
                throw ConflictException(errorCode)
            }
        }
    }
}
