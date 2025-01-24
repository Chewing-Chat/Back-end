package org.chewing.v1.error

class NotFoundException(val errorCode: ErrorCode) : RuntimeException()
