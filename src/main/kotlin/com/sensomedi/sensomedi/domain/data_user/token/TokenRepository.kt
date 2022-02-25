package com.sensomedi.sensomedi.domain.data_user.token

import org.springframework.data.jpa.repository.JpaRepository

interface TokenRepository:JpaRepository<TokenInfo, Long> {
    fun findByAccessToken(accessToken:String?):TokenInfo?
    fun existsByAccessToken(accessToken:String?):Boolean
    fun findByRefreshToken(refreshToken:String?):TokenInfo?
    fun existsByRefreshToken(refreshToken:String?):Boolean
}