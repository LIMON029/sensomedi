package com.sensomedi.sensomedi.config.security

import com.sensomedi.sensomedi.domain.data_user.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepo: UserRepository):UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepo.findByUsername(username?:"") ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다.")
        return CustomUserDetails(user)
    }
}