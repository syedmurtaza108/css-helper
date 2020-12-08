package com.syedmurtaza.css.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syedmurtaza.css.models.*
import com.syedmurtaza.css.utils.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class NotesViewModel(private val firebaseDatabase: FirebaseDatabase) : ViewModel() {

    private val _notes = MutableSharedFlow<List<Note>>()
    val notes = _notes.asSharedFlow()
    private val _subjects = MutableStateFlow<List<Subject>>(listOf())
    val subjects = _subjects.asStateFlow()
    private val _searchedNotes = MutableStateFlow<List<Note>>(listOf())
    val searchedNotes = _searchedNotes.asStateFlow()

    init {
        viewModelScope.launch {
            firebaseDatabase.flowSubject.collect {
                _subjects.value = it.map { response ->
                    return@map response.toSubject()
                }
            }
        }
    }

    fun getNotes(subjectId: String) {
        viewModelScope.launch {
            firebaseDatabase.flowNotes(subjectId).collect {
                _notes.emit(it.map { response ->
                    return@map response.toNote()
                })
            }
        }
    }

    fun addNote(note: Note, onResult: (Boolean) -> Unit) {
        firebaseDatabase.addNote(note, onResult)
    }

    fun updateNote(note: Note, onResult: (Boolean) -> Unit) {
        firebaseDatabase.updateNote(note, onResult)
    }

    fun deleteNote(id: String, onResult: (Boolean) -> Unit) {
        firebaseDatabase.deleteNote(id, onResult)
    }

    fun searchNote(topic: String) {
        viewModelScope.launch {
            firebaseDatabase.searchNote(topic).collect {
                _searchedNotes.value = it.map { response ->
                    return@map response.toNote()
                }
            }
        }

    }
}