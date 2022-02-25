package com.sensomedi.sensomedi.service

import com.sensomedi.sensomedi.domain.data_file.FileInfo
import com.sensomedi.sensomedi.domain.data_file.FileInfoBuilder
import com.sensomedi.sensomedi.domain.data_file.FileRepository
import com.sensomedi.sensomedi.domain.data_user.Roles
import com.sensomedi.sensomedi.domain.data_user.UserInfo
import com.sensomedi.sensomedi.service.dto.FileRequestDto
import com.sensomedi.sensomedi.service.dto.FileResponseDto.*
import com.sensomedi.sensomedi.utils.ERROR_CODE
import com.sensomedi.sensomedi.utils.ERROR_CODE.OK
import com.sensomedi.sensomedi.utils.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.FileCopyUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors

@Service
class FileService(
    @Autowired private val fileRepo: FileRepository,
    private val jdbcTemplate: JdbcTemplate
) {
    @Transactional
    fun upload(file: MultipartFile, user: UserInfo):FileInfo? {
        val fullFileName = (file.originalFilename?:"").split('.')
        if(fileRepo.findByName(fullFileName[0])!=null) return null
        Log.info(file.contentType.toString())
        val fileInfo = FileInfoBuilder()
            .setData(file.bytes)
            .setSize(file.size)
            .setName(fullFileName[0])
            .setType(fullFileName[1])
            .setUser(user)
            .build()
        return fileRepo.save(fileInfo)
    }

    @Transactional
    fun download(fileDto: FileRequestDto.FileDownloadRequestDto, user: UserInfo): Int {
        val file = fileRepo.findById(fileDto.getFileId()).get()
        val fileOwner = file.getUser().getUsername()
        if(user.getUsername() != fileOwner && user.getRole()!=Roles.ROLE_ADMIN)   return ERROR_CODE.DOWNLOAD_ACCESS_DENIED
        val format = SimpleDateFormat("yyyyMMddHHmmss")
        val nowTime = format.format(Calendar.getInstance().time)
        val saveFile = File(fileDto.getPath(), "${file.getName()}_$nowTime.${file.getType()}")
        FileCopyUtils.copy(file.getData(), saveFile)
        return OK
    }

    fun viewAll(user:UserInfo):List<FilesViewResponseDto>{
        if(user.getRole()==Roles.ROLE_ADMIN){
            return fileRepo.findAll().stream()
                .map { file -> FilesViewResponseDto(file)}
                .collect(Collectors.toList())
        }
        return fileRepo.findByUser(user).stream()
            .map { file -> FilesViewResponseDto(file)}
            .collect(Collectors.toList())
    }

    fun viewAllByAdminWithId(user:UserInfo?):List<FilesViewResponseDto>?{
        if(user==null)  return null
        return fileRepo.findByUser(user).stream()
            .map { file -> FilesViewResponseDto(file)}
            .collect(Collectors.toList())
    }

    fun deleteFilesByUser(user:UserInfo){
        val files = fileRepo.findByUser(user)
        for(file in files){
            fileRepo.deleteById(file.getId())
        }
        afterDelete()
    }

    private fun afterDelete() {
        val allFiles = fileRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
        val max = allFiles.size

        for(i:Int in 1..max){
            if(allFiles[i-1].getId()!=i.toLong()){
                jdbcTemplate.update("update fileinfo set id=? where id=?",i.toLong(), allFiles[i-1].getId())
            }
        }

        val sql = "ALTER TABLE fileinfo auto_increment=${max+1}"
        jdbcTemplate.execute(sql)
    }
}