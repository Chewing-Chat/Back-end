package org.chewing.v1.error

class ConflictException(val errorCode: ErrorCode) : RuntimeException()
