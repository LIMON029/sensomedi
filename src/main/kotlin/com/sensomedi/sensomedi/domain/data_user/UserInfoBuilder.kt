package com.sensomedi.sensomedi.domain.data_user

class UserInfoBuilder {
    private lateinit var email:String
    private lateinit var username:String
    private lateinit var password:String

    fun setEmail(email:String): UserInfoBuilder {
        this.email = email
        return this
    }

    fun setUsername(username:String): UserInfoBuilder {
        this.username = username
        return this
    }

    fun setPassword(password:String): UserInfoBuilder {
        this.password = password
        return this
    }

    fun build() = UserInfo(
        email = email,
        username = username,
        password = password
    )
}