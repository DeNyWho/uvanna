package com.example.uvanna.service

import com.example.uvanna.jpa.Orders
import com.example.uvanna.repository.email.EmailRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import javax.mail.Message
import javax.mail.internet.InternetAddress

@Service
class EmailService: EmailRepository {

    @Value("\${spring.mail.username}")
    lateinit var sender: String

    @Autowired
    lateinit var templateEngine: SpringTemplateEngine

    @Autowired
    var emailSender: JavaMailSender? = null

    fun sendSimpleMessage(to: String, subject: String, htmlMsg: String) {
        try {
            val helper = MimeMessagePreparator {    mimeMessage ->
                mimeMessage.setFrom(InternetAddress(sender))
                mimeMessage.setRecipient(Message.RecipientType.TO, InternetAddress(to))
                mimeMessage.setSubject(subject, "UTF-8")
                mimeMessage.setContent(htmlMsg, "text/html; charset=UTF-8")
            }
            emailSender!!.send(helper)
        } catch (exception: MailException) {
            exception.printStackTrace()
        }
    }

    override fun sendSimpleMessageUsingTemplate(
        to: String,
        subject: String,
        template: String,
        params: MutableMap<String, Any>
    ) {
        val context = Context()
        context.setVariables(params)
        val html: String = templateEngine.process(template, context)
        val helper = MimeMessagePreparator { mimeMessage ->
            mimeMessage.setFrom(InternetAddress(sender))
            mimeMessage.setRecipient(Message.RecipientType.TO, InternetAddress(to))
            mimeMessage.setSubject(subject, "UTF-8")
            mimeMessage.setContent(html, "text/html; charset=UTF-8")
        }
        emailSender!!.send(helper)
    }
    fun sendNewOrderMessage(paymentInfo: Orders) {
        var params:MutableMap<String, Any> = mutableMapOf()

        params["price"] = paymentInfo.price
        params["code"] = paymentInfo.code
        params["findOrder"] = "https://uvanna.store/order/findOrder?code=${paymentInfo.code}"

        sendSimpleMessageUsingTemplate(paymentInfo.email, "Новый заказ", "newOrder", params)
    }

    fun sendOrderMessage(paymentInfo: Orders, title: String, template: String) {
        var params:MutableMap<String, Any> = mutableMapOf()

        params["price"] = paymentInfo.price
        params["code"] = paymentInfo.code
        params["findOrder"] = "https://uvanna.store/order/findOrder?code=${paymentInfo.code}"

        sendSimpleMessageUsingTemplate(paymentInfo.email, title, template, params)
    }

}