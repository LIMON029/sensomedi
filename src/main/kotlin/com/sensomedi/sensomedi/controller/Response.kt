package com.sensomedi.sensomedi.controller

open class Response(code: Int, message: String) {
    private val code:Int
    private val message:String

    init {
        this.code = code
        this.message = message
    }

    fun getCode():Int = code
    fun getMessage():String = message
}