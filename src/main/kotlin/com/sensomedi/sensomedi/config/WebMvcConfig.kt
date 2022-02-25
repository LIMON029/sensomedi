package com.sensomedi.sensomedi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig:WebMvcConfigurer {
    @Bean
    fun multipartResolver():CommonsMultipartResolver {
        val commonsMultipartResolver = CommonsMultipartResolver()
        commonsMultipartResolver.setDefaultEncoding("UTF-8")
        commonsMultipartResolver.setMaxUploadSizePerFile(10*1024*1024)
        return commonsMultipartResolver
    }
}