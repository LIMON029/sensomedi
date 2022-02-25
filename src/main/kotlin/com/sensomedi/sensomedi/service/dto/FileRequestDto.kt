package com.sensomedi.sensomedi.service.dto

class FileRequestDto {
    class FileDownloadRequestDto {
        private var fileId:Long = 0
        private lateinit var path:String

        constructor()
        constructor(fileId:Long, path:String) {
            this.fileId = fileId
            this.path = path
        }

        fun getFileId():Long = fileId
        fun getPath():String = path
    }
}