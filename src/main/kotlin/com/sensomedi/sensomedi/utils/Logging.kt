package com.sensomedi.sensomedi.utils

import mu.KotlinLogging

object Log {
    private val Log = KotlinLogging.logger {}
    fun info(msg:String) {
        Log.info(msg)
    }

    fun warn(msg:String) {
        Log.warn(msg)
    }

    fun debug(msg:String) {
        Log.debug(msg)
    }

    fun error(msg:String) {
        Log.error(msg)
    }

    fun error(msg:String, e:Exception) {
        Log.error(msg, e)
    }

    fun trace(msg:String) {
        Log.trace(msg)
    }
}