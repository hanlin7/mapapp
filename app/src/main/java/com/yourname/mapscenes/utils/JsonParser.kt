//package com.yourname.mapscenes.utils
//
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//
//object JsonParser {
//    // 将 private 改为 internal 或 public
//    internal val json = Json { ignoreUnknownKeys = true }
//
//    fun <T> encodeToListString(list: List<T>): String {
//        return json.encodeToString(list)
//    }
//
//    inline fun <reified T> decodeFromListString(jsonString: String): List<T> {
//        return if (jsonString.isBlank()) emptyList()
//        else json.decodeFromString(jsonString)
//    }
//}