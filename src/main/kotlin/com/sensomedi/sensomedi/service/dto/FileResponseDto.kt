package com.sensomedi.sensomedi.service.dto

import com.sensomedi.sensomedi.domain.data_file.FileInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FileResponseDto {
    class FilesViewResponseDto {
        private var id:Long ?= 0L
        private lateinit var fileName:String
        private lateinit var data:String
        private lateinit var owner:String
        private lateinit var modifiedDate:String

        constructor()
        constructor(file: FileInfo){
            val filetype = file.getType()
            this.id = file.getId()
            this.fileName = file.getName() +"."+ filetype
            if(filetype=="txt") this.data = String(file.getData(), Charsets.UTF_8)
            else this.data = "파일 타입이 ${filetype}이므로 파일내용을 표시하지 않습니다. 다운로드 후 확인해주세요."
            this.owner = file.getUser().getUsername()
            this.modifiedDate = formatting(file.modifiedDate)
        }

        private fun formatting(date:LocalDateTime):String {
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
            return date.format(formatter)
        }

        fun getFileName():String = fileName
        fun getData():String = data
        fun getUsername():String = owner
        fun getModifiedDate():String = modifiedDate

        fun setFileName(fileName:String):FilesViewResponseDto{
            this.fileName = fileName
            return this
        }

        fun setData(data:String):FilesViewResponseDto{
            this.data = data
            return this
        }

        fun setUsername(username:String):FilesViewResponseDto{
            this.owner = username
            return this
        }

        fun setModifiedDate(date:LocalDateTime):FilesViewResponseDto{
            this.modifiedDate = formatting(date)
            return this
        }

        override fun toString(): String {
            return "{ 파일명 : $fileName, 소유자 : $owner, 마지막 수정 시간 : $modifiedDate, 파일 내용 : $data }"
        }
    }
}