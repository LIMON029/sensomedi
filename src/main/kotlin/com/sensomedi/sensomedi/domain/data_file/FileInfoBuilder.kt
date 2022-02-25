package com.sensomedi.sensomedi.domain.data_file

import com.sensomedi.sensomedi.domain.data_user.UserInfo

class FileInfoBuilder {
    private lateinit var name:String
    private lateinit var type:String
    private var size:Long = 0L
    private lateinit var data:ByteArray
    private lateinit var user:UserInfo

    fun setName(name:String): FileInfoBuilder {
        this.name = name
        return this
    }

    fun setType(type:String): FileInfoBuilder {
        this.type = type
        return this
    }

    fun setSize(size:Long): FileInfoBuilder {
        this.size = size
        return this
    }

    fun setData(data:ByteArray): FileInfoBuilder {
        this.data = data
        return this
    }

    fun setUser(user:UserInfo): FileInfoBuilder {
        this.user = user
        return this
    }

    fun build() = FileInfo(
        fileName = this.name,
        type = this.type,
        size = this.size,
        data = this.data,
        user = this.user
    )
}