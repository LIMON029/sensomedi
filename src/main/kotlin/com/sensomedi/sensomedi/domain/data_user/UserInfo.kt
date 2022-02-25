package com.sensomedi.sensomedi.domain.data_user

import com.sensomedi.sensomedi.domain.BaseTimeEntity
import com.sensomedi.sensomedi.utils.CONST.LOGOUT
import javax.persistence.*

@Table(name = "userinfo")
@Entity
class UserInfo: BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id:Long = 0L

    @Column(nullable = false)
    private lateinit var email:String

    @Column(nullable = false)
    private lateinit var username:String

    @Column(nullable = false)
    private lateinit var password:String

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private var role: Roles = Roles.ROLE_USER

    @Column(nullable = false)
    private var status:String = LOGOUT

    constructor()
    constructor(email: String, username: String, password:String){
        this.email=email
        this.username=username
        this.password=password
    }

    fun getId():Long = id
    fun getUsername():String = username
    fun getPassword():String = password
    fun getEmail():String = email
    fun getRole():Roles = role
    fun getStatus():String = status

    fun setPassword(newPassword:String){this.password = newPassword }
    fun setRole(role:Roles) { this.role = role }
    fun setUsername(username:String) { this.username = username }
    fun setEmail(email:String) { this.email = email }
    fun setStatus(status:String):UserInfo {
        this.status = status
        return this
    }

    override fun toString(): String {
        return "User(id:$id, username:$username, email:$email, role:$role)"
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false

        other as UserInfo
        if(other.id != id)  return false

        return true
    }
}