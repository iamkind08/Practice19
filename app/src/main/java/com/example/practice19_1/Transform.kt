package com.example.practice19_1

object Transform {
    fun parseIntOrDefault(whatParse: String?, defaultValue: Int): Int {
        return whatParse?.toIntOrNull() ?: defaultValue
    }
}