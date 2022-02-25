package com.sensomedi.sensomedi.service.dto

class UserRequestDto {
    class UserJoinRequestDto(email:String, username:String, password:String, passwordCheck:String, code:Int){
        private var email:String
        private var username:String
        private var password:String
        private var passwordCheck:String
        private var code:Int

        init {
            this.email = email
            this.username = username
            this.password = password
            this.passwordCheck = passwordCheck
            this.code = code
        }

        fun getEmail():String = email
        fun getUsername():String = username
        fun getPassword():String = password
        fun getPasswordCheck():String = passwordCheck
        fun getCode():Int = code

        fun setEmail(email:String) { this.email = email }
        fun setUsername(username:String) { this.username = username}
        fun setPassword(password:String) { this.password = password }
        fun setPasswordCheck(passwordCheck:String) { this.passwordCheck = passwordCheck }
        fun setCode(code:Int) { this.code = code }
    }

    class UserLoginRequestDto{
        private lateinit var username:String
        private lateinit var password:String

        constructor()
        constructor(username:String, password:String) {
            this.username = username
            this.password = password
        }

        fun getUsername():String = username
        fun getPassword():String = password

        fun setUsername(username:String) { this.username = username }
        fun setPassword(password:String) { this.password = password }
    }

    class UserPasswordUpdateRequestDto(password: String, passwordCheck: String){
        private val password:String
        private val passwordCheck:String

        init {
            this.password = password
            this.passwordCheck = passwordCheck
        }

        fun getPassword():String = password
        fun getPasswordCheck():String = passwordCheck
    }

    class UserDeleteRequestDto {
        private lateinit var password:String

        constructor()
        constructor(password: String){
            this.password = password
        }

        fun getPassword():String = password
        fun setPassword(password:String):UserDeleteRequestDto{
            this.password = password
            return this
        }
    }
}