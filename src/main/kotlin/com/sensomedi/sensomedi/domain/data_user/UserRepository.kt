package com.sensomedi.sensomedi.domain.data_user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserInfo, Long> {
    fun findByEmail(email:String):UserInfo?
    fun findByUsername(username:String):UserInfo?
    fun existsByUsername(username:String):Boolean
}