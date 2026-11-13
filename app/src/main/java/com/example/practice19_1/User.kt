package com.example.practice19_1

data class User(
    var profileId: String = "",
    var name: String = "",
    var state: String = "",
    var age: Int = 0,
    var stateSignal: Int = 0,
    var lastMessage: String = "",       // Текст превью (например, "Фото")
    var lastMessageType: String = ""    // Тип: text, image, audio, file
)