package com.syedmurtaza.css.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class VocabularyResponse(
    val id: String = "",
    val word: String = "",
    val meaning: String = "",
    val synonym: String = "",
    val example: String = "",
)

@Parcelize
data class Vocabulary(
    val id: String,
    val word: String,
    val meaning: String,
    val synonym: String,
    val example: String,
) : Parcelable

fun VocabularyResponse.toVocabulary() = Vocabulary(id, word, meaning, synonym, example)