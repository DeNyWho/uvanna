package com.example.uvanna.service

import com.example.uvanna.jpa.Orders
import com.example.uvanna.repository.email.EmailRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessagePreparator
import org.springframework.stereotype.Service
import javax.mail.Message
import javax.mail.internet.InternetAddress

@Service
class EmailService: EmailRepository {

    @Value("\${spring.mail.username}")
    lateinit var sender: String

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

    fun getStarMessage(
        email: String
    ){
        val msg = "Я звезда в своём city (Эй)\n" +
                "С улицы прямо в сети (Эй)\n" +
                "Высоко — Москва-Сити (Эй)\n" +
                "(Ай-ай) Во мне элементы всех стихий (У)\n" +
                "Трахаю сук, пишу стихи (Вау)\n" +
                "Сам иду на эти грехи (У)\n" +
                "Сука — любовь, я как Михей (Lil Buda)\n" +
                "Gang-gang, братик, what's up? (Gang-gang)\n" +
                "Полина, Кристина, what's up? (Gang-gang)\n" +
                "Мы танцуем на разбитых сердцах (Да)\n" +
                "Влюбляюсь снова, я не чувствую (У) страх (Let's go)\n"
        sendSimpleMessage(email, "Братик Воссап", msg)
    }

    fun sendNewOrderMessage(paymentInfo: Orders) {
        val msg = "Сформирован новый заказ с кодом ${paymentInfo.code}"
        sendSimpleMessage(paymentInfo.email, "Новый заказ", msg)
    }

}