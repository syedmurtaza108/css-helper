package com.syedmurtaza.css.ui.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syedmurtaza.css.models.Vocabulary
import com.syedmurtaza.css.models.toVocabulary
import com.syedmurtaza.css.utils.FirebaseDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class VocabularyViewModel(private val firebaseDatabase: FirebaseDatabase) : ViewModel() {

    private val _vocabulary = MutableSharedFlow<List<Vocabulary>>()
    val vocabulary = _vocabulary.asSharedFlow()
    private val _searchedVocabulary = MutableStateFlow<List<Vocabulary>>(listOf())
    val searchedVocabulary = _searchedVocabulary.asStateFlow()
    private val _isRefresh = MutableSharedFlow<Boolean>()
    val isRefresh = _isRefresh.asSharedFlow()

    init {
        reload()
    }

    fun reload() {
        viewModelScope.launch {
            firebaseDatabase.flowVocabulary.collect {
                _vocabulary.emit(it.map { response ->
                    return@map response.toVocabulary()
                })
                _isRefresh.emit(false)
            }
        }
    }

    fun addVocabulary(vocabulary: Vocabulary, onResult: (Boolean) -> Unit) {
        firebaseDatabase.addVocabulary(vocabulary, onResult)
    }

    fun updateVocabulary(vocabulary: Vocabulary, onResult: (Boolean) -> Unit) {
        firebaseDatabase.updateVocabulary(vocabulary, onResult)
    }

    fun deleteVocabulary(id: String, onResult: (Boolean) -> Unit) {
        firebaseDatabase.deleteVocabulary(id, onResult)
    }

    fun searchVocabulary(word: String) {
        viewModelScope.launch {
            firebaseDatabase.searchVocabulary(word).collect {
                _searchedVocabulary.value = it.map { response ->
                    return@map response.toVocabulary()
                }
            }
        }

    }
}