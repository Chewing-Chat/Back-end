package org.chewing.v1.model.contact

import org.chewing.v1.model.auth.ValidationCode

sealed class Contact {
    abstract val validationCode: ValidationCode
}
