package com.sensomedi.sensomedi.config

import com.sensomedi.sensomedi.config.handler.CustomAuthenticationEntryPoint
import com.sensomedi.sensomedi.config.jwt.JwtAuthenticationFilter
import com.sensomedi.sensomedi.config.jwt.JwtAuthorizationFilter
import com.sensomedi.sensomedi.domain.data_user.Roles
import com.sensomedi.sensomedi.service.TokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val tokenService: TokenService,
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint
): WebSecurityConfigurerAdapter() {
    @Bean
    fun encodePwd():BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(http: HttpSecurity?) {
        if(http==null) return
        http.cors().and().csrf().disable()
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http.formLogin().disable()
            .logout().disable()
        http.httpBasic().authenticationEntryPoint(authenticationEntryPoint)
        http.addFilter(JwtAuthenticationFilter(authenticationManager(), tokenService))
            .addFilter(JwtAuthorizationFilter(authenticationManager(), tokenService))
        http.authorizeRequests()
//            .antMatchers("/error-page/**").permitAll()
            .antMatchers("/favicon.ico").permitAll()
            .antMatchers("/test", "/login", "/join/**", "/api/access", "/api/sendTempPassword").permitAll()
            .antMatchers("/api/user/**").access("hasRole('${rolesToRole(Roles.ROLE_ADMIN)}') or hasRole('${rolesToRole(Roles.ROLE_USER)}')")
            .antMatchers("/api/admin/**").access("hasRole('${rolesToRole(Roles.ROLE_ADMIN)}')")
            .anyRequest().authenticated()
    }

    override fun configure(web: WebSecurity?) {
        web?.ignoring()
            ?.antMatchers("/css/**", "/js/**")
            ?.antMatchers("/favicon.ico", "/test", "/join/**", "/api/access", "/api/sendTempPassword")
            ?.antMatchers("/error-page/**")
    }

    private fun rolesToRole(roles: Roles):String {
        return when(roles){
            Roles.ROLE_USER -> "USER"
            Roles.ROLE_ADMIN -> "ADMIN"
        }
    }
}