package com.sensomedi.sensomedi.config.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.sensomedi.sensomedi.controller.FileResponse
import com.sensomedi.sensomedi.controller.TokenResponse
import com.sensomedi.sensomedi.utils.ERROR_CODE
import com.sensomedi.sensomedi.utils.StatusUtils.statusMsg
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class CustomAuthenticationEntryPoint:AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        response?.characterEncoding = "UTF-8"
        val out = response?.writer
        val om = ObjectMapper()
        if(response?.status!= ERROR_CODE.UNAUTHORIZED_ERROR
            && response?.status!= ERROR_CODE.UNKNOWN_TOKEN
            && response?.status!= ERROR_CODE.TOKEN_EXPIRED_ERROR)
        {
            val text = om.writeValueAsString(TokenResponse(ERROR_CODE.LOGIN_FAILED, "로그인 실패", "", ""))
            out?.println(text)
            out?.close()
        }
        out?.println(om.writeValueAsString(FileResponse(response.status, statusMsg(response.status)?:"", "")))
    }
}