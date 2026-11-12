package com.example.practice19_1

data class User(
    var name: String,
    var state: String,
    var age: Int,
    var stateSignal: Int // 0 - офлайн, 1 - онлайн, 2 - отошел
)