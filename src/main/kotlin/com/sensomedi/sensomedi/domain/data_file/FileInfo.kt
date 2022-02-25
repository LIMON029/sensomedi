package com.sensomedi.sensomedi.domain.data_file

import com.sensomedi.sensomedi.domain.BaseTimeEntity
import com.sensomedi.sensomedi.domain.data_user.UserInfo
import javax.persistence.*

@Entity
@Table(name="fileinfo")
class FileInfo: BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id:Long = 0L

    @Column(nullable = false)
    private lateinit var name:String

    @Column(nullable = false)
    private lateinit var type:String

    @Column(nullable = false)
    private var byteSize:Long = 0L

    @Lob
    private lateinit var data:ByteArray

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private lateinit var user:UserInfo

    constructor()
    constructor(fileName:String, type:String, size: Long, data: ByteArray, user: UserInfo){
        this.name = fileName
        this.type = type
        this.byteSize = size
        this.data = data
        this.user = user
    }

    fun getId():Long = id
    fun getType():String = type
    fun getSize():Long = byteSize
    fun getData():ByteArray = data
    fun getUser():UserInfo = user
    fun getName():String = name

    fun setType(type:String) { this.type = type }
    fun setSize(size:Long) { this.byteSize = size }
    fun setData(data:ByteArray) { this.data = data }
    fun setUser(user:UserInfo) { this.user = user }
    fun setName(name:String) { this.name = name }

    override fun toString(): String {
        return "File(id:$id, type:$type, size:$byteSize, user:${user.getId()})"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(this===other) return true

        if(javaClass != other?.javaClass) return false

        other as FileInfo

        if(other.id != this.id) return false

        return true
    }
}