package com.example.practice19_1

object UserStaticInfo {
    val users = mutableListOf<User>()
    const val POSITION = "position"

    init {
        if (users.isEmpty()) {
            users.add(User(name = "Иван", state = "online", age = 19, stateSignal = 1,
                lastMessage = "Фото", lastMessageType = "image"))
            users.add(User(name = "Иван", state = "offline", age = 19, stateSignal = 0,
                lastMessage = "Привет, как дела?", lastMessageType = "text"))
            users.add(User(name = "Иван", state = "away", age = 19, stateSignal = 2,
                lastMessage = "Аудиосообщение", lastMessageType = "audio"))
            users.add(User(name = "Иван", state = "offline", age = 19, stateSignal = 0,
                lastMessage = "Файл: договор.pdf", lastMessageType = "file"))
        }
    }
}