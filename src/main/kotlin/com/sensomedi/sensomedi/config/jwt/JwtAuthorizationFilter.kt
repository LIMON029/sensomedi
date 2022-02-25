package com.sensomedi.sensomedi.config.jwt

import com.sensomedi.sensomedi.service.TokenService
import com.sensomedi.sensomedi.utils.CONST.HEADER_STRING
import com.sensomedi.sensomedi.utils.CONST.TOKEN_PREFIX
import com.sensomedi.sensomedi.utils.ERROR_CODE
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// 인증이 필요한 모든 요청에 대해 작동하는 필터
class JwtAuthorizationFilter(authenticationManager:AuthenticationManager, private val tokenService: TokenService)
    : BasicAuthenticationFilter(authenticationManager) {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if(request.requestURI.contains("error"))   return
        val jwtHeader = request.getHeader(HEADER_STRING)

        if(jwtHeader == null || !jwtHeader.startsWith(TOKEN_PREFIX)){
            response.status = ERROR_CODE.UNAUTHORIZED_ERROR
            chain.doFilter(request, response)
            return
        }

        val jwtToken = request.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "")

        if(!tokenService.existsByAccessToken(jwtToken)){
            response.status = ERROR_CODE.UNKNOWN_TOKEN
            chain.doFilter(request, response)
            return
        }

        if(tokenService.isExpiredAccessToken(jwtToken)){
            response.status = ERROR_CODE.TOKEN_EXPIRED_ERROR
            chain.doFilter(request, response)
            return
        }

        val authentication = tokenService.getAuthentication(jwtToken)
        if(authentication == null){
            response.status = ERROR_CODE.UNAUTHORIZED_ERROR
            chain.doFilter(request, response)
            return
        }

        SecurityContextHolder.getContext().authentication = authentication
        chain.doFilter(request, response)
    }
}