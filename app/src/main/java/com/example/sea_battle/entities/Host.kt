package com.example.sea_battle.entities

import java.net.Socket

data class Host(val name: String, val timeBound: Int, val isPublic: Boolean, val password: String?, val socket: Socket)