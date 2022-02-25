package com.sensomedi.sensomedi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.sensomedi.sensomedi.domain.data_user.UserRepository
import com.sensomedi.sensomedi.service.dto.MailDto
import com.sensomedi.sensomedi.service.dto.UserRequestDto
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class indexControllerTest(
    @LocalServerPort private val port:Int
){
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var context: WebApplicationContext
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var mvc:MockMvc

    @BeforeEach
    fun setup() {
        mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder?>(springSecurity())
            .build()
    }

    @AfterEach
    fun refresh() {
        val size = userRepository.count()
        userRepository.deleteById(size)
        val sql = "ALTER TABLE userinfo auto_increment=${size}"
        jdbcTemplate.execute(sql)
    }

    @Test
    fun join() {
        val sendUrl = "http://localhost:${port}/join/sendCheckEmail"
        val mailDto = MailDto("test")
        val result = mvc.perform(MockMvcRequestBuilders.post(sendUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(mailDto)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val code = result.response.contentAsString.split(":").last()
            .replace("\"","")
            .replace("}", "").toInt()
        val userJoinDto = UserRequestDto.UserJoinRequestDto(
            email = "bbb@gmail.com",
            username = "bbb",
            password = "bbbbbbbbb",
            passwordCheck = "bbbbbbbbb",
            code = code
        )
        val url = "http://localhost:${port}/join"

        val result1 = mvc.perform(MockMvcRequestBuilders.post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(ObjectMapper().writeValueAsString(userJoinDto)))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        println(result1.response.contentAsString)
    }
}