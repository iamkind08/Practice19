package com.example.practice19_1

object UserStaticInfo {
    val users = mutableListOf<User>()
    const val POSITION = "position"

    init {
        if (users.isEmpty()) {
            // Используем именованные аргументы, чтобы не путаться в порядке
            users.add(User(name = "Иван", state = "Я усталь", age = 19, stateSignal = 0))
            users.add(User(name = "Иван", state = "Я усталь", age = 19, stateSignal = 1))
            users.add(User(name = "Иван", state = "Я усталь", age = 19, stateSignal = 2))
            users.add(User(name = "Иван", state = "Я усталь", age = 19, stateSignal = 0))
        }
    }
}