package com.example.uvanna.repository.email

interface EmailRepository {
    fun sendSimpleMessageUsingTemplate(to: String, subject: String, template: String, params: MutableMap<String, Any>)
}