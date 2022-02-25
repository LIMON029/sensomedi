package com.sensomedi.sensomedi.service

import com.sensomedi.sensomedi.config.jwt.JwtUtil
import com.sensomedi.sensomedi.config.jwt.UserAuthentication
import com.sensomedi.sensomedi.domain.data_user.UserInfo
import com.sensomedi.sensomedi.domain.data_user.UserRepository
import com.sensomedi.sensomedi.domain.data_user.token.ExpiredToken
import com.sensomedi.sensomedi.domain.data_user.token.ExpiredTokenRepository
import com.sensomedi.sensomedi.domain.data_user.token.TokenInfo
import com.sensomedi.sensomedi.domain.data_user.token.TokenRepository
import com.sensomedi.sensomedi.utils.ERROR_CODE
import com.sensomedi.sensomedi.utils.Log
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

@Service
class TokenService(
    private val tokenRepo: TokenRepository,
    private val userRepo:UserRepository,
    private val expiredTokenRepo: ExpiredTokenRepository,
    private val jdbcTemplate: JdbcTemplate
) {
    private val jwtUtil = JwtUtil()

    @Transactional
    fun saveToken(user: UserInfo, request: HttpServletRequest):TokenInfo{
        val expiredToken = jwtUtil.resolveRefreshToken(request)
        if(expiredToken!=null && expiredToken.isNotBlank()) expireToken(expiredToken)

        val accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole())
        val refreshToken = jwtUtil.createRefreshToken(user.getUsername())
        return if(tokenRepo.existsByRefreshToken(refreshToken)){
            recreateToken(refreshToken) as TokenInfo
        } else {
            tokenRepo.save(TokenInfo(refreshToken, accessToken))
        }
    }

    fun existsByAccessToken(accessToken: String):Boolean {
        return tokenRepo.existsByAccessToken(accessToken)
    }

    fun checkUser(username: String):Boolean {
        return userRepo.existsByUsername(username)
    }

    fun updateUser(user:UserInfo):UserInfo{
        return userRepo.save(user)
    }

    fun findUserById(userId:Long):UserInfo? {
        return userRepo.findById(userId).orElse(null)
    }

    fun findUserByUsername(username: String):UserInfo? {
        return userRepo.findByUsername(username)
    }

    fun getAuthorities(username: String): List<GrantedAuthority> {
        val user = userRepo.findByUsername(username) ?: return emptyList()
        return listOf<GrantedAuthority>(SimpleGrantedAuthority(user.getRole().name))
    }

    fun getAuthentication(token:String): UserAuthentication? {
        if(!tokenRepo.existsByAccessToken(token))   return null
        val username = jwtUtil.getUsername(token) ?: ""
        if(username=="")    return null
        val user = userRepo.findByUsername(username) ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다.")
        return UserAuthentication(username, "", listOf(SimpleGrantedAuthority(user.getRole().name)))
    }

    @Transactional
    fun recreateToken(refreshToken: String): Any {
        if(!tokenRepo.existsByRefreshToken(refreshToken)) return ERROR_CODE.UNKNOWN_TOKEN
        val username = jwtUtil.getUsername(refreshToken) ?: return ERROR_CODE.TOKEN_UNKNOWN_USER
        val user = userRepo.findByUsername(username) ?: return ERROR_CODE.TOKEN_UNKNOWN_USER
        if(isExpiredRefreshToken(refreshToken)) return ERROR_CODE.TOKEN_EXPIRED_ERROR

        expireToken(refreshToken)
        val newAccessToken = jwtUtil.createAccessToken(username, user.getRole())
        val newRefreshToken = jwtUtil.createRefreshToken(username)

        return tokenRepo.save(TokenInfo(newRefreshToken, newAccessToken))
    }

    @Transactional
    fun deleteByRefreshToken(refreshToken: String){
        val id = tokenRepo.findByRefreshToken(refreshToken)?.getId() ?: return
        tokenRepo.deleteById(id)
        afterDelete()
    }

    @Transactional
    fun deleteByAccessToken(accessToken: String){
        val token = tokenRepo.findByAccessToken(accessToken) ?: return
        expireTokenRepoSave(token.getRefreshToken())
        val id = token.getId()
        tokenRepo.deleteById(id)
        afterDelete()
    }

    @Transactional
    fun expireToken(refreshToken: String):ExpiredToken {
        deleteByRefreshToken(refreshToken)
        Log.info("expired")
        return expireTokenRepoSave(refreshToken)
    }

    fun expireTokenRepoSave(refreshToken: String):ExpiredToken {
        if(expiredTokenRepo.count() >= 500){
            for(i:Int in 1..10){
                expiredTokenRepo.deleteById(i.toLong())
            }
            afterDelete()
        }
        return expiredTokenRepo.save(ExpiredToken(refreshToken))
    }

    fun afterDelete(){
        val allToken = tokenRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
        val max = allToken.size

        for(i:Int in 1..max){
            if(allToken[i-1].getId()!=i.toLong()){
                jdbcTemplate.update("update tokeninfo set id=? where id=?", i.toLong(), allToken[i-1].getId())
            }
        }
        val sql = "ALTER TABLE tokeninfo auto_increment=${max+1}"
        jdbcTemplate.execute(sql)
    }

    fun isExpiredRefreshToken(token:String):Boolean {
        return expiredTokenRepo.existsByToken(token)
    }

    fun isExpiredAccessToken(token:String):Boolean {
        return !jwtUtil.validateToken(token)
    }
}