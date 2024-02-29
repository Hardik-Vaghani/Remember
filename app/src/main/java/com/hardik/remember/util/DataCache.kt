package com.hardik.remember.util

object DataCache {
    var cachedData: List<*>? = null
}
inline fun <reified T> DataCache.setData(data: List<T>) {
    cachedData = data
}
