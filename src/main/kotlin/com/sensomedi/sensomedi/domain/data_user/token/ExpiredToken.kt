package com.sensomedi.sensomedi.domain.data_user.token

import javax.persistence.*

@Entity
@Table(name="expiredtoken")
class ExpiredToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id:Long = 0L

    @Column(nullable = false)
    private lateinit var token:String

    constructor()
    constructor(token: String){
        this.token = token
    }

    fun getId():Long = id
    fun getToken():String = token
    fun setToken(token:String) { this.token = token }
}