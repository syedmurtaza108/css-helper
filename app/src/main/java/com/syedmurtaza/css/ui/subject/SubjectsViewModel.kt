package com.syedmurtaza.css.ui.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syedmurtaza.css.models.*
import com.syedmurtaza.css.utils.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SubjectsViewModel(private val firebaseDatabase: FirebaseDatabase) : ViewModel() {

    private val _subjects = MutableStateFlow<List<Subject>>(listOf())
    val subjects = _subjects.asStateFlow()
    private val _notes = MutableStateFlow<List<NoteResponse>>(listOf())
    val notes = _notes.asStateFlow()
    private val _isRefresh = MutableSharedFlow<Boolean>()
    val isRefresh = _isRefresh.asSharedFlow()

    init {
        reload()
        getNotes()
    }

    fun reload() {
        viewModelScope.launch {
            firebaseDatabase.flowSubject.collect {
                _subjects.value = it.map { response ->
                    return@map response.toSubject()
                }
                _isRefresh.emit(false)
            }
        }
    }

    private fun getNotes(){
        viewModelScope.launch {
            firebaseDatabase.flowAllNotes().collect {
                _notes.value = it
            }
        }
    }

    fun addSubject(subject: Subject, onResult: (Boolean) -> Unit) {
        firebaseDatabase.addSubject(subject, onResult)
    }

    fun updateSubject(subject: Subject, onResult: (Boolean) -> Unit) {
        firebaseDatabase.updateSubject(subject, onResult)
    }

    fun deleteSubject(id: String, onResult: (Boolean) -> Unit) {
        firebaseDatabase.deleteSubject(id, onResult)
    }
}