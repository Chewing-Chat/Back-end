package org.chewing.v1.error

class AuthorizationException(val errorCode: ErrorCode) : RuntimeException()
