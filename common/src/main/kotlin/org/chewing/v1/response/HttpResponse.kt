package org.chewing.v1.response

data class HttpResponse<T>(
    val status: Int,
    val data: T,
) {
    companion object {
        fun <T> success(data: T): HttpResponse<T> {
            return HttpResponse(200, data)
        }

        fun successOnly(): HttpResponse<SuccessOnlyResponse> {
            return HttpResponse(200, SuccessOnlyResponse())
        }

        fun successCreate(): HttpResponse<SuccessCreateResponse> {
            return HttpResponse(201, SuccessCreateResponse())
        }

        fun <T> error(status: Int, data: T): HttpResponse<T> {
            return HttpResponse(status, data)
        }
    }
}
