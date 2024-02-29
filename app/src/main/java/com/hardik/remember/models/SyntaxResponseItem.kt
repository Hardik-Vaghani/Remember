package com.hardik.remember.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import java.io.Serializable

@Keep
data class SyntaxResponseItem(
    @SerializedName("data")
    @Expose
    val `data`: List<Data> = listOf(),
    @SerializedName("example")
    @Expose
    val example: String = "", // She reads a book.
    @SerializedName("id")
    @Expose
    val id: Int = 0, // 1
    @SerializedName("sentence_type")
    @Expose
    val sentenceType: String = "" // Present Simple tense
) :Serializable {
    @Keep
    data class Data(
        @SerializedName("example")
        @Expose
        val example: String = "", // She eats lunch at 12 PM.
        @SerializedName("id")
        @Expose
        val id: Int = 0, // 1
        @SerializedName("meaning")
        @Expose
        val meaning: String = "",
        @SerializedName("syntax")
        @Expose
        val syntax: String = "", // Subject + Base Verb (s/es for third person singular)
        @SerializedName("type")
        @Expose
        val type: String = "" // Positive
    ): Serializable
}