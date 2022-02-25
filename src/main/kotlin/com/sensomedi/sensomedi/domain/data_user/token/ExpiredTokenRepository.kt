package com.sensomedi.sensomedi.domain.data_user.token

import org.springframework.data.jpa.repository.JpaRepository

interface ExpiredTokenRepository:JpaRepository<ExpiredToken, Long> {
    fun findByToken(token:String?):ExpiredToken?
    fun existsByToken(token:String?):Boolean
}