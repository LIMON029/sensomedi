package com.sensomedi.sensomedi.service.dto

class MailDto(
    private var address:String
) {
    fun getAddress():String = address

    fun setAddress(address: String):MailDto {
        this.address = address
        return this
    }
}