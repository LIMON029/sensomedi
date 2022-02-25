package com.sensomedi.sensomedi.config.jwt

import com.sensomedi.sensomedi.domain.data_user.Roles
import com.sensomedi.sensomedi.utils.CONST.ACCESS_TOKEN
import com.sensomedi.sensomedi.utils.CONST.REFRESH_TOKEN
import com.sensomedi.sensomedi.utils.Log
import io.jsonwebtoken.*
import java.util.*
import javax.servlet.http.HttpServletRequest

class JwtUtil {

    fun createAccessToken(username:String, role:Roles):String {
        val claims = Jwts.claims().setSubject(username)
        claims["role"] = role
        val now = Date()
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + ACCESS_TOKEN_VALID_TIME))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }

    fun createRefreshToken(username:String):String {
        val claims = Jwts.claims().setSubject(username)
        val now = Date()
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + REFRESH_TOKEN_VALID_TIME))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }

    fun getUsername(token:String):String? {
        return try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body.subject
        } catch (e:Exception){
            null
        }
    }

    fun resolveAccessToken(request:HttpServletRequest):String? {
        return request.getHeader(ACCESS_TOKEN)
    }

    fun resolveRefreshToken(request: HttpServletRequest):String? {
        return request.getHeader(REFRESH_TOKEN)
    }

    fun validateToken(token:String):Boolean {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token)
            return true
        } catch (e:SignatureException){
            Log.error("Invalid JWT signature")
        } catch (e:MalformedJwtException){
            Log.error("Invalid JWT token")
        } catch (e:ExpiredJwtException){
            Log.error("Expired JWT token")
        } catch (e:UnsupportedJwtException){
            Log.error("Unsupported JWT token")
        } catch (e:IllegalArgumentException){
            Log.error("JWT claims string is empty.")
        }
        return false
    }

    companion object {
        private const val SECRET_KEY = "secret"

        private const val MIN = 1000L * 60
        private const val HOUR = MIN * 60
        private const val DAY = HOUR * 24
        private const val MONTH = DAY * 30

        private const val ACCESS_TOKEN_VALID_TIME:Long = HOUR
        private const val REFRESH_TOKEN_VALID_TIME:Long = MONTH * 3
    }
}