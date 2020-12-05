package com.syedmurtaza.css.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class SubjectResponse (val id:String = "", val name:String = "")

@Parcelize
data class Subject(val id:String, val name:String):Parcelable

fun SubjectResponse.toSubject() = Subject(id,name)
