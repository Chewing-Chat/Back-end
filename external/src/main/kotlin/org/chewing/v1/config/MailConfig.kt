package org.chewing.v1.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfig(
    @Value("\${spring.mail.host}")
    private val host: String,

    @Value("\${spring.mail.port}")
    private val port: String,

    @Value("\${spring.mail.username}")
    private val username: String,

    @Value("\${spring.mail.password}")
    private val password: String,
) {

    @Bean
    fun javaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port.toInt()
        mailSender.username = username
        mailSender.password = password
        val props = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.ssl.trust"] = "smtp.gmail.com"
        props["mail.smtp.ssl.protocols"] = "TLSv1.2"
        mailSender.javaMailProperties = props
        return mailSender
    }
}
