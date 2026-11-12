package com.example.practice19_1

object UserStaticInfo {
    val users = mutableListOf<User>()
    const val POSITION = "position"

    init {
        if (users.isEmpty()) {
            users.add(User("Иван", "Я усталь", 19, 0))
            users.add(User("Иван", "Я усталь", 19, 1))
            users.add(User("Иван", "Я усталь", 19, 2))
            users.add(User("Иван", "Я усталь", 19, 0))
        }
    }
}