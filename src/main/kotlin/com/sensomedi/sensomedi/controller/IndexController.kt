package com.sensomedi.sensomedi.controller

import com.sensomedi.sensomedi.domain.data_user.token.TokenInfo
import com.sensomedi.sensomedi.service.MailService
import com.sensomedi.sensomedi.service.TokenService
import com.sensomedi.sensomedi.service.UserService
import com.sensomedi.sensomedi.service.dto.MailDto
import com.sensomedi.sensomedi.service.dto.UserRequestDto.*
import com.sensomedi.sensomedi.utils.CONST.FOR_JOIN_CODE
import com.sensomedi.sensomedi.utils.CONST.FOR_TEMP_PASSWORD
import com.sensomedi.sensomedi.utils.CONST.HEADER_STRING
import com.sensomedi.sensomedi.utils.CONST.LOGIN
import com.sensomedi.sensomedi.utils.CONST.LOGOUT
import com.sensomedi.sensomedi.utils.CONST.REFRESH_TOKEN_PREFIX
import com.sensomedi.sensomedi.utils.CONST.TOKEN_PREFIX
import com.sensomedi.sensomedi.utils.ERROR_CODE
import com.sensomedi.sensomedi.utils.StatusUtils.checkUser
import com.sensomedi.sensomedi.utils.StatusUtils.failedStatusSet
import com.sensomedi.sensomedi.utils.StatusUtils.statusMsg
import org.springframework.web.bind.annotation.*
import java.security.SecureRandom
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class IndexController(
    private val userService: UserService,
    private val mailService: MailService,
    private val tokenService: TokenService
) {
    private val checkCode:MutableMap<String, Int> = mutableMapOf()

    @GetMapping("/test")
    fun main(response: HttpServletResponse):FileResponse {
        return FileResponse(ERROR_CODE.OK,"main_ci_1", "")
    }

    // 테스트용 url
    @GetMapping("/api/user")
    fun user(request: HttpServletRequest):FileResponse {
        val jwtToken = request.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "")
        val username = tokenService.getAuthentication(jwtToken)?.getUsername() ?: "UNKNOWN"
        return FileResponse(ERROR_CODE.OK, "username:${username}","")
    }

    // 토큰 재발급
    @PostMapping("/api/access")
    fun access(request: HttpServletRequest, response: HttpServletResponse):TokenResponse {
        val jwtHeader = request.getHeader(HEADER_STRING)
        if(jwtHeader==null){
            response.status = ERROR_CODE.UNAUTHORIZED_ERROR
            return TokenResponse(ERROR_CODE.UNAUTHORIZED_ERROR, "토큰 재발급 실패","", "")
        }
        if(jwtHeader.startsWith(REFRESH_TOKEN_PREFIX)) {
            val jwtToken = jwtHeader.replace(REFRESH_TOKEN_PREFIX, "")
            val token = tokenService.recreateToken(jwtToken)
            if(token.javaClass == TokenInfo().javaClass) {
                token as TokenInfo
                response.status = HttpServletResponse.SC_OK
                return TokenResponse(ERROR_CODE.OK, "토큰 재발급", token.getAccessToken(), token.getRefreshToken())
            }
            val fileResponse = failedStatusSet(token as Int)
            return TokenResponse(fileResponse.getCode(), fileResponse.getMessage(), "","")
        }
        val fileResponse = failedStatusSet(ERROR_CODE.TOKEN_RECREATE_ERROR)
        return TokenResponse(fileResponse.getCode(), fileResponse.getMessage(), "","")
    }


    // 임시 비밀번호 발급
    @PostMapping("/api/sendTempPassword")
    fun sendTempPassword(@RequestBody mailDto: MailDto):FileResponse {
        val tempPassword = generateRandomString()
        val user = userService.findByEmail(mailDto.getAddress()) ?: return failedStatusSet(ERROR_CODE.UNKNOWN_USER)
        if(user.getStatus()== LOGIN)    return failedStatusSet(ERROR_CODE.SEND_TEMP_PASSWORD_FAILED)
        userService.update(user, tempPassword)
        if (mailDto.getAddress() == "test") {
            return FileResponse(ERROR_CODE.OK, "테스트 성공. 임시 비밀번호 :$tempPassword","")
        }
        val result = mailService.mailSend(mailDto, tempPassword, FOR_TEMP_PASSWORD)
        return if(result==ERROR_CODE.OK) FileResponse(ERROR_CODE.OK, "임시 비밀번호를 성공적으로 전송했습니다.","")
        else return FileResponse(result, "잘못된 이메일 주소입니다.","")
    }

    // 비밀번호 변경
    @PostMapping("/api/user/update")
    fun updatePassword(@RequestBody reqDto:UserPasswordUpdateRequestDto, request: HttpServletRequest):FileResponse{
        val user = checkUser(request, tokenService)
            ?: return FileResponse(ERROR_CODE.TOKEN_UNKNOWN_USER, "유저 정보가 잘못되어 회원 정보 수정에 실패했습니다.","")
        val result = userService.update(user, reqDto.getPassword(), reqDto.getPasswordCheck())
        if(result == ERROR_CODE.OK) {
            return FileResponse(ERROR_CODE.OK, "회원 정보를 성공적으로 수정하였습니다.","")
        }
        return failedStatusSet(result)
    }

    // 회원 가입
    @PostMapping("/join")
    fun join(@RequestBody joinDto:UserJoinRequestDto, response:HttpServletResponse):FileResponse {
        val result = userService.join(joinDto, checkCode[joinDto.getEmail()])
        response.status = HttpServletResponse.SC_OK
        if(result==ERROR_CODE.OK) {
            checkCode.remove(joinDto.getEmail())
            return FileResponse(ERROR_CODE.OK,"회원가입을 성공적으로 완료했습니다.","")
        }
        return FileResponse(result,"회원가입을 하는 도중 오류가 발생했습니다. -> ${statusMsg(result)}","")
    }

    // 인증 메일 보내기
    @PostMapping("/join/sendCheckEmail")
    fun sendCheckEmail(@RequestBody mailDto: MailDto, response: HttpServletResponse):FileResponse {
        val random = SecureRandom()
        val code = random.nextInt(1000000)
        response.status = HttpServletResponse.SC_OK
        if(mailDto.getAddress()=="test") {
            checkCode["bbb@gmail.com"] = code
            return FileResponse(ERROR_CODE.OK,"테스트 성공.","$code")
        }
        checkCode[mailDto.getAddress()] = code
        val result = mailService.mailSend(mailDto, code.toString(), FOR_JOIN_CODE)
        return if(result==ERROR_CODE.OK) FileResponse(ERROR_CODE.OK,"인증메일을 성공적으로 전송했습니다.","")
        else FileResponse(result, "잘못된 이메일 주소입니다.", "")
    }

    // 로그아웃 (로그인 과정은 config > jwt > JwtAuthenticationFilter.kt)
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest):FileResponse {
        val user = checkUser(request, tokenService)
            ?:return FileResponse(ERROR_CODE.TOKEN_UNKNOWN_USER, "로그아웃 실패 -> ${statusMsg(ERROR_CODE.TOKEN_UNKNOWN_USER)}","")
        user.setStatus(LOGOUT)
        tokenService.updateUser(user)
        tokenService.deleteByAccessToken(request.getHeader(HEADER_STRING).replace(TOKEN_PREFIX,""))
        return FileResponse(ERROR_CODE.OK, "로그아웃 성공","")
    }

    @PostMapping("/api/user/delete")
    fun deleteUser(@RequestBody dto:UserDeleteRequestDto, request: HttpServletRequest):FileResponse {
        val user = checkUser(request, tokenService)
            ?:return FileResponse(ERROR_CODE.TOKEN_UNKNOWN_USER, "회원 탈퇴 실패 -> ${statusMsg(ERROR_CODE.TOKEN_UNKNOWN_USER)}","")
        val result = userService.delete(user, dto.getPassword())
        if(result==ERROR_CODE.OK){
            tokenService.deleteByAccessToken(request.getHeader(HEADER_STRING).replace(TOKEN_PREFIX,""))
            return FileResponse(ERROR_CODE.OK, "회원 탈퇴 성공","")
        }
        return failedStatusSet(result)
    }

    // 임시 비밀번호 생성 함수
    private fun generateRandomString():String {
        return List(8) {
            (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
        }.joinToString("")
    }
}