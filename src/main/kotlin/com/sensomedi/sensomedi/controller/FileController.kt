package com.sensomedi.sensomedi.controller

import com.sensomedi.sensomedi.service.FileService
import com.sensomedi.sensomedi.service.TokenService
import com.sensomedi.sensomedi.service.dto.FileRequestDto.FileDownloadRequestDto
import com.sensomedi.sensomedi.utils.ERROR_CODE
import com.sensomedi.sensomedi.utils.StatusUtils.checkUser
import com.sensomedi.sensomedi.utils.StatusUtils.failedStatusSet
import com.sensomedi.sensomedi.utils.StatusUtils.statusMsg
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class FileController(
    @Autowired private val fileService: FileService,
    @Autowired private val tokenService: TokenService
) {
    @PostMapping("/api/user/upload")
    fun upload(@RequestParam("file") file: MultipartFile, request: HttpServletRequest, response: HttpServletResponse):FileResponse {
        val user = checkUser(request, tokenService)
            ?: return FileResponse(ERROR_CODE.TOKEN_UNKNOWN_USER,"업로드 도중 오류가 발생했습니다. -> ${statusMsg(ERROR_CODE.TOKEN_UNKNOWN_USER)}","")
        val fileInfo = fileService.upload(file, user)
            ?: return FileResponse(ERROR_CODE.EXIST_FILE_NAME,"업로드 도중 오류가 발생했습니다. -> ${statusMsg(ERROR_CODE.EXIST_FILE_NAME)}","")
        return FileResponse(ERROR_CODE.OK,"업로드를 성공적으로 완료했습니다.","")
    }

    // 일반 유저 : 자신의 파일 열람
    // 어드인 유저 : 모든 파일 열람
    @PostMapping("/api/user/view")
    fun view(request: HttpServletRequest, response: HttpServletResponse):FileResponse {
        val user = checkUser(request, tokenService)
            ?: return FileResponse(ERROR_CODE.TOKEN_UNKNOWN_USER,"파일 열람 도중 오류가 발생했습니다. -> ${statusMsg(ERROR_CODE.TOKEN_UNKNOWN_USER)}","")
        val files = fileService.viewAll(user)
        if(files.isEmpty()) return failedStatusSet(ERROR_CODE.NO_FILE_FOR_VIEW)
        return FileResponse(ERROR_CODE.OK,"파일 열람 성공", files.toString())
    }

    // 어드민 유저가 유저 아이디로 파일 열람
    @PostMapping("/api/admin/view/{id}")
    fun viewWithId(@PathVariable("id") id:Long, request: HttpServletRequest, response: HttpServletResponse):FileResponse{
        checkUser(request, tokenService)
            ?: return FileResponse(ERROR_CODE.TOKEN_UNKNOWN_USER,"파일 열람 도중 오류가 발생했습니다. -> ${statusMsg(ERROR_CODE.TOKEN_UNKNOWN_USER)}","")
        val files = fileService.viewAllByAdminWithId(tokenService.findUserById(id))
            ?: return failedStatusSet(ERROR_CODE.VIEW_ACCESS_DENIED)
        if(files.isEmpty()) return failedStatusSet(ERROR_CODE.NO_FILE_FOR_VIEW)
        return FileResponse(ERROR_CODE.OK,"파일 열람 성공", files.toString())
    }

    // 파일 다운로드
    @PostMapping("/api/user/download")
    fun download(@RequestBody fileDto: FileDownloadRequestDto, request: HttpServletRequest, response: HttpServletResponse):FileResponse {
        val user = checkUser(request, tokenService)
            ?: return FileResponse(ERROR_CODE.TOKEN_UNKNOWN_USER,"다운로드 도중 오류가 발생했습니다. -> ${statusMsg(ERROR_CODE.TOKEN_UNKNOWN_USER)}","")
        if(fileService.download(fileDto, user) == ERROR_CODE.DOWNLOAD_ACCESS_DENIED) {
            response.status = ERROR_CODE.DOWNLOAD_ACCESS_DENIED
            return FileResponse(ERROR_CODE.DOWNLOAD_ACCESS_DENIED, "다운로드 도중 오류가 발생했습니다. -> 다른 유저의 파일입니다.","")
        }
        return FileResponse(ERROR_CODE.OK,"다운로드를 성공적으로 완료했습니다.","")
    }
}