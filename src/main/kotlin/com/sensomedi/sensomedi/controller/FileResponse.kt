package com.sensomedi.sensomedi.controller

class FileResponse(code:Int, message:String, data:String):Response(code, message) {
    private val data:String

    init {
        this.data = data
    }

    fun getData():String = data
}