package com.sensomedi.sensomedi.domain.data_user.token

import javax.persistence.*

@Table(name="tokeninfo")
@Entity
class TokenInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id:Long = 0L

    @Column(nullable = false)
    private lateinit var accessToken:String

    @Column(nullable = false)
    private lateinit var refreshToken:String

    constructor()
    constructor(refreshToken:String, accessToken:String){
        this.refreshToken = refreshToken
        this.accessToken = accessToken
    }

    fun getId():Long = id
    fun getAccessToken():String = accessToken
    fun getRefreshToken():String = refreshToken
    fun setAccessToken(token:String) { this.accessToken = token }
    fun setRefreshToken(token:String) { this.refreshToken = token }

    override fun toString(): String {
        return "AccessToken: $accessToken\nRefreshToken: $refreshToken"
    }
}