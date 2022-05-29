package com.example.sea_battle.entities

data class Task(val tag: String, val data: String){
    constructor(string: String) : this(string.split("\n")[0], string.split("\n")[1])
}