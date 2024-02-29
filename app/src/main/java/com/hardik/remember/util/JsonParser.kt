package com.hardik.remember.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.io.InputStream

class JsonParser {
    inline fun <reified T> parseJson(inputStream: InputStream): T {
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, Charsets.UTF_8)

        return Gson().fromJson(json, object : TypeToken<T>() {}.type)
    }
}
