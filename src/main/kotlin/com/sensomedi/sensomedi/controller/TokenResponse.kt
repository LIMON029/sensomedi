package com.sensomedi.sensomedi.controller

class TokenResponse(code:Int, message:String, accessToken:String, refreshToken:String): Response(code, message) {
    private val accessToken:String
    private val refreshToken:String

    init {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun getAccessToken():String = accessToken
    fun getRefreshToken():String = refreshToken
}