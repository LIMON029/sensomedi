package com.sensomedi.sensomedi.utils

import com.sensomedi.sensomedi.controller.FileResponse
import com.sensomedi.sensomedi.controller.Response
import com.sensomedi.sensomedi.domain.data_user.UserInfo
import com.sensomedi.sensomedi.service.TokenService
import com.sensomedi.sensomedi.utils.CONST.HEADER_STRING
import com.sensomedi.sensomedi.utils.CONST.TOKEN_PREFIX
import javax.servlet.http.HttpServletRequest

object StatusUtils {
    private val StatusMsg:Map<Int, String> =
        mapOf(
            ERROR_CODE.CODE_NOT_EXIST to "인증 코드가 존재하지 않습니다.",
            ERROR_CODE.CODE_NOT_MATCHED to "인증 코드가 다릅니다.",
            ERROR_CODE.EMAIL_EXIST to "이미 존재하는 이메일입니다.",
            ERROR_CODE.USERNAME_EXIST to "이미 존재하는 닉네임입니다.",
            ERROR_CODE.PASSWORD_NOT_MATCHED to "비밀번호가 다릅니다.",
            ERROR_CODE.PASSWORD_TOO_SHORT to "비밀번호는 8자 이하가 될 수 없습니다.",
            ERROR_CODE.UNAUTHORIZED_ERROR to "인증되지 않은 접근입니다.",
            ERROR_CODE.TOKEN_RECREATE_ERROR to "토큰 재생성 실패",
            ERROR_CODE.TOKEN_EXPIRED_ERROR to "만료된 토큰입니다.",
            ERROR_CODE.UNKNOWN_TOKEN to "유효하지 않은 토큰입니다.",
            ERROR_CODE.TOKEN_UNKNOWN_USER to "유저 정보가 잘못되었습니다.",
            ERROR_CODE.UNKNOWN_USER to "해당 이메일을 가진 유저가 존재하지 않습니다.",
            ERROR_CODE.SEND_TEMP_PASSWORD_FAILED to "로그인된 상태에서는 임시 비밀번호를 발급할 수 없습니다.",
            ERROR_CODE.VIEW_ACCESS_DENIED to "해당 아이디를 가진 유저가 존재하지 않습니다.",
            ERROR_CODE.NO_FILE_FOR_VIEW to "파일이 존재하지 않습니다.",
            ERROR_CODE.EXIST_FILE_NAME to "이미 존재하는 파일명입니다."
        )

    fun statusMsg(errorCode:Int):String? {
        return StatusMsg[errorCode]
    }

    // 요청 실패 시 response 설정 함수
    fun failedStatusSet(errorCode:Int): FileResponse {
        if(errorCode == ERROR_CODE.PASSWORD_NOT_MATCHED_FOR_UPDATE
            || errorCode == ERROR_CODE.PASSWORD_TOO_SHORT_FOR_UPDATE){
            val code = if(errorCode==ERROR_CODE.PASSWORD_TOO_SHORT_FOR_UPDATE) ERROR_CODE.PASSWORD_TOO_SHORT else ERROR_CODE.PASSWORD_NOT_MATCHED
            return FileResponse(code, statusMsg(code)?:"","")
        }
        if(errorCode == ERROR_CODE.PASSWORD_NOT_MATCHED_FOR_DELETE){
            val code = ERROR_CODE.PASSWORD_NOT_MATCHED
            return FileResponse(code, statusMsg(code)?:"","")
        }
        val message = when(errorCode){
            ERROR_CODE.TOKEN_EXPIRED_ERROR,
            ERROR_CODE.UNKNOWN_TOKEN,
            ERROR_CODE.TOKEN_UNKNOWN_USER,
            ERROR_CODE.TOKEN_RECREATE_ERROR -> {
                "토큰 재발급 실패"
            }
            ERROR_CODE.CODE_NOT_EXIST,
            ERROR_CODE.EMAIL_EXIST,
            ERROR_CODE.USERNAME_EXIST,
            ERROR_CODE.PASSWORD_NOT_MATCHED,
            ERROR_CODE.PASSWORD_TOO_SHORT,
            ERROR_CODE.CODE_NOT_MATCHED -> {
                "회원가입을 하는 도중 오류가 발생했습니다."
            }
            ERROR_CODE.UNKNOWN_USER,
            ERROR_CODE.SEND_TEMP_PASSWORD_FAILED -> {
                "임시 비밀번호 발급 도중 오류가 발생했습니다."
            }
            ERROR_CODE.VIEW_ACCESS_DENIED,
            ERROR_CODE.NO_FILE_FOR_VIEW -> {
                "파일 열람 도중 오류가 발생했습니다."
            }
            else -> ""
        }
        return FileResponse(errorCode, message,"")
    }

    fun checkUser(request: HttpServletRequest, tokenService: TokenService): UserInfo? {
        val token = request.getHeader(HEADER_STRING).replace(TOKEN_PREFIX, "")
        val userAuthentication = tokenService.getAuthentication(token)
        return tokenService.findUserByUsername(userAuthentication?.getUsername() ?: "")
    }
}