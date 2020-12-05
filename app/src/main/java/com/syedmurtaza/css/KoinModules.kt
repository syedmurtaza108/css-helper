package com.syedmurtaza.css

import com.syedmurtaza.css.ui.notes.NotesViewModel
import com.syedmurtaza.css.utils.FirebaseDatabase
import com.syedmurtaza.css.ui.subject.SubjectsViewModel
import com.syedmurtaza.css.ui.vocabulary.VocabularyViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModules by lazy {
    listOf(
        module,
    )
}

@ExperimentalCoroutinesApi
val module = module {
    viewModel {
        SubjectsViewModel(get())
    }
    viewModel {
        VocabularyViewModel(get())
    }
    viewModel {
        NotesViewModel(get())
    }
    single {
        FirebaseDatabase()
    }
}

