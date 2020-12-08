package com.syedmurtaza.css.ui.notes

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialContainerTransform
import com.syedmurtaza.css.R
import com.syedmurtaza.css.databinding.FragmentNotesBinding
import com.syedmurtaza.css.models.Note
import com.syedmurtaza.css.models.Subject
import com.syedmurtaza.css.ui.subject.SubjectNotesAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class NotesFragment : Fragment() {
    private lateinit var binding: FragmentNotesBinding
    private val viewModel: NotesViewModel by viewModel()
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        val subject = bundle.getParcelable<Subject>(KEY_SUBJECT_BUNDLE)
        viewModel.getNotes(subject?.id!!)

        binding.idTextView.text = "#" + subject.id
        binding.subjectNameView.text = subject.name

        binding.appBar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            duration = 300
            scrimColor = Color.TRANSPARENT
        }

        notesAdapter = NotesAdapter({
            findNavController().navigate(R.id.action_notesFragment_to_noteDetailFragment,
                bundleOf(NoteDialogFragment.KEY_NOTE_CURRENT to it))
        }, {
            val dialog = NoteDialogFragment { note ->
                viewModel.updateNote(note = note) { isAdded ->
                    if (isAdded)
                        Toast.makeText(context,
                            "Note has been updated successfully.",
                            Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context,
                            "Note has NOT been updated. Try Again!",
                            Toast.LENGTH_SHORT).show()
                }
            }
            dialog.arguments = bundleOf(
                NoteDialogFragment.KEY_NOTE_CURRENT to it,
                NoteDialogFragment.KEY_STRING_SUBJECT_ID to subject.id)
            dialog.show(parentFragmentManager, "")
            return@NotesAdapter true
        })

        val subjectNotesAdapter = SubjectNotesAdapter {
            findNavController().navigate(R.id.action_notesFragment_self,
                bundleOf(KEY_SUBJECT_BUNDLE to it))
        }

        binding.notesList.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = notesAdapter
        }

        binding.notesSubjectsList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = subjectNotesAdapter
        }

        binding.appBar.setOnMenuItemClickListener {
            if (it.itemId == R.id.search_menu) {
                binding.notesSearchView.isVisible = true
                binding.notesSearchView.isIconified = false
            } else {
                val dialog = NoteDialogFragment { note ->
                    viewModel.addNote(note) { isAdded ->
                        if (isAdded)
                            Toast.makeText(context,
                                "Note has been added successfully.",
                                Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(context,
                                "Note has NOT been added. Try again",
                                Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.arguments =
                    bundleOf(NoteDialogFragment.KEY_STRING_SUBJECT_ID to subject.id)
                dialog.show(parentFragmentManager, "")
            }
            return@setOnMenuItemClickListener true
        }


        binding.notesSearchView.setOnCloseListener {
            binding.notesSearchView.isVisible = false
            return@setOnCloseListener false
        }

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int,
            ) {
                deleteConfirmationDialog((viewHolder as NotesAdapter.NoteViewHolder).note!!,
                    notesAdapter).show()
            }

        }).attachToRecyclerView(binding.notesList)

        binding.notesSearchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.searchNote(query ?: "")
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.notes.collect {
                notesAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.subjects.collect {
                subjectNotesAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.searchedNotes.collect {
                notesAdapter.submitList(it)
            }
        }
    }

    private fun deleteConfirmationDialog(note: Note, adapter: NotesAdapter): AlertDialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Note would be deleted permanently.")
                setMessage("Are you sure to delete the selected note?")
                setPositiveButton("Yes",
                    DialogInterface.OnClickListener { _, _ ->
                        viewModel.deleteNote(note.id) { response ->
                            if (response)
                                Toast.makeText(context,
                                    "Note has been deleted successfully.",
                                    Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(context,
                                    "Note has NOT been deleted. Try Again!",
                                    Toast.LENGTH_SHORT).show()
                        }
                    })
                setNegativeButton("No",
                    DialogInterface.OnClickListener { _, _ ->
                        adapter.notifyDataSetChanged()
                    })
            }
            builder.create()
        }

    }

    companion object {
        const val KEY_SUBJECT_BUNDLE = "subject"
    }
}