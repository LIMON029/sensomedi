package com.sensomedi.sensomedi.service

import com.sensomedi.sensomedi.service.dto.MailDto
import com.sensomedi.sensomedi.utils.CONST.FOR_JOIN_CODE
import com.sensomedi.sensomedi.utils.CONST.FOR_TEMP_PASSWORD
import com.sensomedi.sensomedi.utils.ERROR_CODE
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine

@Service
class MailService(
    private val javaMailSender:JavaMailSender,
    private val templateEngine: SpringTemplateEngine
) {
    private val TITLE: Map<String, String> by lazy {
        mapOf(FOR_JOIN_CODE to "[센소메디] 가입 인증 메일", FOR_TEMP_PASSWORD to "[센소메디] 임시 비밀번호 발급")
    }

    fun mailSend(mail:MailDto, code:String, codename: String):Int {
        val message = javaMailSender.createMimeMessage()
        val messageHelper = MimeMessageHelper(message)
        try {
            messageHelper.setTo(mail.getAddress())
            messageHelper.setSubject(TITLE[codename]?:"")
            if(codename==FOR_JOIN_CODE && code!="test") messageHelper.setText(setContext(String.format("%06d", code.toInt()), codename), true)
            messageHelper.setText(setContext(code, codename), true)
            javaMailSender.send(message)
        } catch (e: Exception){
            return ERROR_CODE.ILLEGAL_EMAIL
        }
        return ERROR_CODE.OK
    }

    private fun setContext(code:String, codename:String):String {
        val context = Context()
        context.setVariable("code", code)
        return if(codename==FOR_JOIN_CODE) templateEngine.process("joinEmailForm", context)
        else templateEngine.process("updateEmailForm", context)
    }
}