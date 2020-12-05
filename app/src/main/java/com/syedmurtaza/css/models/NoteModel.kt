package com.syedmurtaza.css.models

import android.os.Parcelable
import com.syedmurtaza.css.ui.notes.Point
import kotlinx.parcelize.Parcelize


data class NoteResponse(
    val id: String = "",
    val subjectId: String = "",
    val topic: String = "",
    val points: List<Point> = listOf(),
)

@Parcelize
data class Note(
    val id: String,
    val subjectId: String,
    val topic: String,
    val points: List<Point>,
) : Parcelable

fun NoteResponse.toNote() = Note(id, subjectId, topic, points)