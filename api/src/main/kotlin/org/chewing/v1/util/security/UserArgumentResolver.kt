package org.chewing.v1.util.security

import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.user.UserId
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import kotlin.jvm.java

class UserArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val supported = parameter.hasParameterAnnotation(CurrentUser::class.java)
        return supported
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw AuthorizationException(ErrorCode.NOT_AUTHORIZED)

        val userId = authentication.principal as? UserId
            ?: throw AuthorizationException(ErrorCode.NOT_AUTHORIZED)

        return userId.id
    }
}
