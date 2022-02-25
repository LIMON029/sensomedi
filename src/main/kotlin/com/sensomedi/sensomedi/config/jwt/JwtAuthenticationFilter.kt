package com.sensomedi.sensomedi.config.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.sensomedi.sensomedi.config.security.CustomUserDetails
import com.sensomedi.sensomedi.controller.TokenResponse
import com.sensomedi.sensomedi.service.TokenService
import com.sensomedi.sensomedi.service.dto.UserRequestDto.*
import com.sensomedi.sensomedi.utils.CONST.LOGIN
import com.sensomedi.sensomedi.utils.ERROR_CODE
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// 로그인 시 적용되는 필터
class JwtAuthenticationFilter(
    private val authenticationManage:AuthenticationManager,
    private val tokenService:TokenService
): UsernamePasswordAuthenticationFilter() {
    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {
        var authenticationToken:UsernamePasswordAuthenticationToken? = null
        val om = ObjectMapper()
        try {
            val userLoginRequestDto = om.readValue(request?.inputStream, UserLoginRequestDto::class.java)
            if(!tokenService.checkUser(userLoginRequestDto.getUsername())) throw Exception("존재하지 않는 유저입니다.")
            authenticationToken = UsernamePasswordAuthenticationToken(
                userLoginRequestDto.getUsername(),
                userLoginRequestDto.getPassword(),
                tokenService.getAuthorities(userLoginRequestDto.getUsername())
            )
        } catch (e:IOException){
            e.printStackTrace()
        }
        return authenticationManage.authenticate(authenticationToken)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        if(request == null) return
        val userDetails = authResult?.principal as CustomUserDetails
        val user = userDetails.getUser()
        if(user.getStatus()== LOGIN){
            response?.status = ERROR_CODE.DUPLICATED_LOGIN_REQUEST_ERROR
            return
        }
        val token = tokenService.saveToken(user, request)
        tokenService.updateUser(user.setStatus(LOGIN))
        response?.status = ERROR_CODE.OK
        response?.characterEncoding = "UTF-8"
        val out = response?.writer
        val om = ObjectMapper()
        val text = om.writeValueAsString(TokenResponse(ERROR_CODE.OK, "로그인 성공", token.getAccessToken(), token.getRefreshToken()))
        out?.println(text)
        out?.close()
    }
}