package com.syedmurtaza.css.utils

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.syedmurtaza.css.models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class FirebaseDatabase {
    private val db = Firebase.firestore

    fun addSubject(subject: Subject, onResult: (Boolean) -> Unit) {
        db.collection(PATH_SUBJECT_STRING).add(subject).addOnCompleteListener {
            val response = it.result
            response?.set(subject.copy(id = response.id))?.addOnCompleteListener { complete ->
                onResult.invoke(complete.isSuccessful && complete.isComplete)
            }
        }
    }

    fun updateSubject(subject: Subject, onResult: (Boolean) -> Unit) {
        db.collection(PATH_SUBJECT_STRING).document(subject.id).set(subject).addOnCompleteListener {
            onResult.invoke(it.isSuccessful && it.isComplete)
        }
    }

    fun deleteSubject(id: String, onResult: (Boolean) -> Unit) {
        db.collection(PATH_SUBJECT_STRING).document(id).delete().addOnCompleteListener {
            onResult(it.isComplete && it.isSuccessful)
        }
    }

    fun addVocabulary(vocabulary: Vocabulary, onResult: (Boolean) -> Unit) {
        db.collection(PATH_VOCABULARY_STRING).add(vocabulary).addOnCompleteListener {
            val response = it.result
            response?.set(vocabulary.copy(id = response.id))?.addOnCompleteListener { complete ->
                onResult.invoke(complete.isSuccessful && complete.isComplete)
            }
        }
    }

    fun updateVocabulary(vocabulary: Vocabulary, onResult: (Boolean) -> Unit) {
        db.collection(PATH_VOCABULARY_STRING).document(vocabulary.id).set(vocabulary)
            .addOnCompleteListener {
                onResult.invoke(it.isSuccessful && it.isComplete)
            }
    }

    fun deleteVocabulary(id: String, onResult: (Boolean) -> Unit) {
        db.collection(PATH_VOCABULARY_STRING).document(id).delete().addOnCompleteListener {
            onResult(it.isComplete && it.isSuccessful)
        }
    }

    fun searchVocabulary(word: String): Flow<List<VocabularyResponse>> {
        return callbackFlow {
            db.collection(PATH_VOCABULARY_STRING)
                .whereGreaterThanOrEqualTo(PROPERTY_WORD_STRING, word)
                .whereLessThanOrEqualTo(PROPERTY_WORD_STRING, word + "\uF7FF")
                .get()
                .addOnCompleteListener { wordTask ->
                    if (wordTask.isSuccessful) {
                        launch {
                            this@callbackFlow.send(wordTask.result!!.toObjects(VocabularyResponse::class.java))
                        }
                    } else {
                        Log.d("Firestore Error", "Error")
                    }
                }
            awaitClose {
                close()
            }
        }
    }

    fun addNote(note: Note, onResult: (Boolean) -> Unit) {
        db.collection(PATH_NOTE_STRING).add(note).addOnCompleteListener {
            val response = it.result
            response?.set(note.copy(id = response.id))?.addOnCompleteListener { complete ->
                onResult.invoke(complete.isSuccessful && complete.isComplete)
            }
        }
    }

    fun updateNote(note: Note, onResult: (Boolean) -> Unit) {
        db.collection(PATH_NOTE_STRING).document(note.id).set(note)
            .addOnCompleteListener {
                onResult.invoke(it.isSuccessful && it.isComplete)
            }
    }

    fun deleteNote(id: String, onResult: (Boolean) -> Unit) {
        db.collection(PATH_NOTE_STRING).document(id).delete().addOnCompleteListener {
            onResult(it.isComplete && it.isSuccessful)
        }
    }

    fun searchNote(topic: String): Flow<List<NoteResponse>> {
        return callbackFlow {
            db.collection(PATH_NOTE_STRING)
                .whereGreaterThanOrEqualTo(PROPERTY_TOPIC_STRING, topic)
                .whereLessThanOrEqualTo(PROPERTY_TOPIC_STRING, topic + "\uF7FF")
                .get()
                .addOnCompleteListener { noteTask ->
                    if (noteTask.isSuccessful) {
                        launch {
                            this@callbackFlow.send(noteTask.result!!.toObjects(NoteResponse::class.java))
                        }
                    } else {
                        Log.d("Firestore Error", "Error")
                    }
                }
            awaitClose {
                close()
            }
        }
    }

    val flowSubject = callbackFlow {
        var tempList = listOf<SubjectResponse>()
        db.collection(PATH_SUBJECT_STRING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore Error", e.message.toString())
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    tempList = snapshot.toObjects()
                }
                launch {
                    this@callbackFlow.send(tempList)
                }
            }

        awaitClose {
            close()
        }
    }

    val flowVocabulary = callbackFlow {
        var tempList = listOf<VocabularyResponse>()
        db.collection(PATH_VOCABULARY_STRING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore Error", e.message.toString())
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    tempList = snapshot.toObjects()
                }
                launch {
                    this@callbackFlow.send(tempList)
                }
            }

        awaitClose {
            close()
        }
    }

    fun flowNotes(subjectId: String): Flow<List<NoteResponse>> {
        return callbackFlow {
            var tempList = listOf<NoteResponse>()
            db.collection(PATH_NOTE_STRING)
                .whereEqualTo(PROPERTY_SUBJECT_ID_STRING, subjectId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("Firestore Error", e.message.toString())
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        tempList = snapshot.toObjects()
                    }
                    launch {
                        this@callbackFlow.send(tempList)
                    }

                }
            awaitClose {
                close()
            }
        }
    }

    fun flowAllNotes(): Flow<List<NoteResponse>> {
        return callbackFlow {
            var tempList = listOf<NoteResponse>()
            db.collection(PATH_NOTE_STRING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("Firestore Error", e.message.toString())
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        tempList = snapshot.toObjects()
                    }
                    launch {
                        this@callbackFlow.send(tempList)
                    }
                }
            awaitClose {
                close()
            }
        }
    }

    companion object {
        const val PATH_SUBJECT_STRING = "subjects"
        const val PATH_VOCABULARY_STRING = "vocabulary"
        const val PATH_NOTE_STRING = "notes"
        const val PROPERTY_WORD_STRING = "word"
        const val PROPERTY_TOPIC_STRING = "topic"
        const val PROPERTY_SUBJECT_ID_STRING = "subjectId"
    }
}