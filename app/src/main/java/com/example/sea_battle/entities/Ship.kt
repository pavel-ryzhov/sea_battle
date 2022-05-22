package com.example.sea_battle.entities

import java.io.Serializable

data class Ship(var x: Int, var y: Int, val type: Int, var rotate: Boolean): Serializable
