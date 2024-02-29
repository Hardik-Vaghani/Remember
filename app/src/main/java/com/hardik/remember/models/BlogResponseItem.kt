package com.hardik.remember.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Keep
@Entity(tableName = "blog", indices = [Index(value = ["title"], unique = true)])
data class BlogResponseItem(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var id: Int = 0,//1
    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String ="",
    @ColumnInfo(name = "content")
    @SerializedName("content")
    var content: String ="",
    @ColumnInfo(name = "type")
    @SerializedName("type")
    var type: String ="",
    @ColumnInfo(name = "is_like")
    @SerializedName("isLike")
    @Expose
    var is_like: Boolean = false,
    @ColumnInfo(name = "datestamp")
    @SerializedName("datestamp")
    val datestamp: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
    @ColumnInfo(name = "timestamp")
    @SerializedName("timestamp")
    val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
)
//HH: Hour in 24-hour format (00-23).
//mm: Minutes (00-59).
//ss: Seconds (00-59).
//SSS: Milliseconds (000-999).
//XXX: Time zone offset in the form of "±HH:mm" (for example, +02:00).
//SimpleDateFormat("HH:mm:ss.SSSXXX", Locale.US).format(Date())































