package com.hardik.remember.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.io.Serializable

@Keep
@Entity(
    tableName = "spelling",
    indices = [Index(value = ["word"], unique = true)]
)
data class SpellingResponseItem(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    val id: Int = 0, // 1
    @ColumnInfo(name = "word")
    @SerializedName("word")
    @Expose
    val word: String = "",
    @ColumnInfo(name = "meaning")
    @SerializedName("meaning")
    @Expose
    val meaning: String = "",
    @ColumnInfo(name = "pronounce")
    @SerializedName("pronounce")
    @Expose
    val pronounce: String = "",
    @ColumnInfo(name = "type")
    @SerializedName("type")
    @Expose
    val type: String = "",
    @ColumnInfo(name = "is_like")
    @SerializedName("isLike")
    @Expose
    val is_like: Boolean = false
):Serializable