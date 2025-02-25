package org.chewing.v1.client

import org.chewing.v1.dto.SmsMessageDto
import org.chewing.v1.service.notification.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class SmsClient(
    @Value("\${ncp.sms.accessKey}")
    private val accessKey: String,

    @Value("\${ncp.sms.secretKey}")
    private val secretKey: String,

    @Value("\${ncp.sms.serviceId}")
    private val serviceId: String,

) {

    private val url: String = "https://sens.apigw.ntruss.com"

    private val log = LoggerFactory.getLogger(NotificationService::class.java)

    @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun send(requestSmsDto: SmsMessageDto): Map<String, Any>? {
        val currentTimeString = System.currentTimeMillis().toString()
        val webClient = WebClient.builder()
            .baseUrl(url)
            .defaultHeader("x-ncp-apigw-timestamp", currentTimeString)
            .defaultHeader("x-ncp-iam-access-key", accessKey)
            .defaultHeader(
                "x-ncp-apigw-signature-v2",
                makeSignature(currentTimeString, "/sms/v2/services/$serviceId/messages", HttpMethod.POST),
            )
            .build()

        val response = webClient.post()
            .uri("/sms/v2/services/$serviceId/messages")
            .bodyValue(requestSmsDto)
            .retrieve()
            .bodyToMono(Map::class.java)
            .block() as? Map<String, Any>

        return response
    }

    @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    private fun makeSignature(
        time: String,
        urlString: String,
        httpMethod: HttpMethod,
    ): String {
        val space = " "
        val newLine = "\n"
        val method = httpMethod.name()
        val accessKey = accessKey
        val secretKey = secretKey

        val message = StringBuilder()
            .append(method)
            .append(space)
            .append(urlString)
            .append(newLine)
            .append(time)
            .append(newLine)
            .append(accessKey)
            .toString()

        val signingKey = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)
        val rawHmac = mac.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(rawHmac)
    }
}
