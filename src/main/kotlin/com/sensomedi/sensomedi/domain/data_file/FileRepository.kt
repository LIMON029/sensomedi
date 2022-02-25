package com.sensomedi.sensomedi.domain.data_file

import com.sensomedi.sensomedi.domain.data_user.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository:JpaRepository<FileInfo, Long> {
    fun findByUser(user:UserInfo):List<FileInfo>
    fun findByName(name:String):FileInfo?
}