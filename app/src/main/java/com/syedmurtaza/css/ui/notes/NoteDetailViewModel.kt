package com.syedmurtaza.css.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syedmurtaza.css.models.Note
import com.syedmurtaza.css.models.toNote
import com.syedmurtaza.css.utils.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class NoteDetailViewModel(private val firebaseDatabase: FirebaseDatabase) : ViewModel() {

    private val _note = MutableStateFlow(Note("", "", "", listOf()))
    val note = _note.asStateFlow()

    fun selectedNote(id: String) {
        viewModelScope.launch {
            firebaseDatabase.flowSelectedNote(id).collect {
                _note.value = it.toNote()
            }
        }
    }

    fun deleteNote(id: String, onResult: (Boolean) -> Unit) {
        firebaseDatabase.deleteNote(id, onResult)
    }

    fun updateNote(note: Note, onResult: (Boolean) -> Unit) {
        firebaseDatabase.updateNote(note, onResult)
    }
}