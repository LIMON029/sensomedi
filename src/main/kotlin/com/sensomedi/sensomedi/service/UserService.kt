package com.sensomedi.sensomedi.service

import com.sensomedi.sensomedi.domain.data_user.UserInfo
import com.sensomedi.sensomedi.domain.data_user.UserInfoBuilder
import com.sensomedi.sensomedi.domain.data_user.UserRepository
import com.sensomedi.sensomedi.service.dto.UserRequestDto.*
import com.sensomedi.sensomedi.utils.ERROR_CODE
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    @Autowired private val userRepo: UserRepository,
    @Autowired private val fileService: FileService,
    @Autowired private val passwordEncoder: BCryptPasswordEncoder,
    private val jdbcTemplate: JdbcTemplate
) {
    fun findByEmail(email:String):UserInfo? {
        return userRepo.findByEmail(email)
    }

    fun findById(id:Long): UserInfo? {
        return userRepo.findById(id).orElse(null)
    }

    fun findByUsername(username:String?):UserInfo? {
        return userRepo.findByUsername(username?:"")
    }

    @Transactional
    fun join(joinDto:UserJoinRequestDto, code:Int?):Int {
        if(code==null) return ERROR_CODE.CODE_NOT_EXIST
        if(userRepo.findByEmail(joinDto.getEmail())!=null)  return ERROR_CODE.EMAIL_EXIST
        if(userRepo.findByUsername(joinDto.getUsername())!=null || joinDto.getUsername()=="master")  return ERROR_CODE.USERNAME_EXIST
        if(joinDto.getPassword().length <= 8)   return ERROR_CODE.PASSWORD_TOO_SHORT
        if(joinDto.getPassword()!=joinDto.getPasswordCheck())   return ERROR_CODE.PASSWORD_NOT_MATCHED
        if(code != joinDto.getCode()) return ERROR_CODE.CODE_NOT_MATCHED
        val newUser = UserInfoBuilder()
            .setEmail(joinDto.getEmail())
            .setPassword(passwordEncoder.encode(joinDto.getPassword()))
            .setUsername(joinDto.getUsername())
            .build()
        userRepo.save(newUser)
        return ERROR_CODE.OK
    }

    @Transactional
    fun update(user:UserInfo, password: String):Int {
        user.setPassword(passwordEncoder.encode(password))
        userRepo.save(user)
        return ERROR_CODE.OK
    }

    @Transactional
    fun update(user:UserInfo, password: String, passwordCheck:String):Int {
        if(password.length <= 8)   return ERROR_CODE.PASSWORD_TOO_SHORT_FOR_UPDATE
        if(password!=passwordCheck) return ERROR_CODE.PASSWORD_NOT_MATCHED_FOR_UPDATE
        return update(user, password)
    }

    @Transactional
    fun delete(user:UserInfo, password:String):Int {
        if(!passwordEncoder.matches(password, user.getPassword()))    return ERROR_CODE.PASSWORD_NOT_MATCHED_FOR_DELETE
        fileService.deleteFilesByUser(user)
        userRepo.deleteById(user.getId())
        afterDelete()
        return ERROR_CODE.OK
    }

    private fun afterDelete() {
        val allUser = userRepo.findAll(Sort.by(Sort.Direction.ASC, "id"))
        val max = allUser.size

        for(i:Int in 1..max){
            if(allUser[i-1].getId()!=i.toLong()){
                jdbcTemplate.update("update userinfo set id=? where id=?",i.toLong(), allUser[i-1].getId())
            }
        }

        val sql = "ALTER TABLE userinfo auto_increment=${max+1}"
        jdbcTemplate.execute(sql)
    }
}