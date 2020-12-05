package com.syedmurtaza.css.ui.subject

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syedmurtaza.css.R
import com.syedmurtaza.css.databinding.FragmentSubjectsBinding
import com.syedmurtaza.css.models.Subject
import com.syedmurtaza.css.ui.notes.NotesFragment
import com.syedmurtaza.css.ui.subject.SubjectsAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class SubjectsFragment : Fragment() {

    private lateinit var binding: FragmentSubjectsBinding
    private var isEdit = false
    private val viewModel: SubjectsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = activity?.getSharedPreferences(
            "ThemePref",
            Context.MODE_PRIVATE
        )
        initTheme(sharedPreferences)

        binding.appBar.setOnMenuItemClickListener { menu ->
            if (menu.itemId == R.id.change_theme_btn) {
                checkTheme(sharedPreferences)
            }
            return@setOnMenuItemClickListener true
        }
        val subjectsAdapter = SubjectsAdapter({
            findNavController().navigate(R.id.action_homeFragment_to_notesFragment,
                bundleOf(NotesFragment.KEY_SUBJECT_BUNDLE to it))
        }, {
            val subjectBottomSheet = SubjectBottomSheet()
            subjectBottomSheet.arguments = bundleOf(SubjectBottomSheet.KEY_ARG_BUNDLE to it)
            subjectBottomSheet.show(parentFragmentManager, "")
            isEdit = true
            return@SubjectsAdapter true
        })

        binding.subjectsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = subjectsAdapter
        }

        binding.swipRefresh.setOnRefreshListener {
            viewModel.reload()
        }

        setFragmentResultListener(KEY_REQUEST_STRING) { _, result: Bundle ->
            if (isEdit) {
                val subject = result.getParcelable<Subject>(KEY_SUBJECT_BUNDLE)
                viewModel.updateSubject(subject!!) {
                    if (it)
                        Toast.makeText(context,
                            "Subject has been updated successfully.",
                            Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context,
                            "Subject has NOT been updated. Try again!",
                            Toast.LENGTH_SHORT).show()
                }
                isEdit = false
            } else {
                val subject = result.getParcelable<Subject>(KEY_SUBJECT_BUNDLE)
                viewModel.addSubject(subject!!) {
                    if (it)
                        Toast.makeText(context,
                            "Subject has been added successfully.",
                            Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context,
                            "Subject has NOT been added. Try again!",
                            Toast.LENGTH_SHORT).show()
                }
            }
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
                deleteConfirmationDialog((viewHolder as SubjectsAdapter.SubjectViewHolder).subject!!,
                    subjectsAdapter)?.show()
            }

        }).attachToRecyclerView(binding.subjectsList)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.isRefresh.collect {
                binding.swipRefresh.isRefreshing = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.subjects.collect {
                val tempList = it.toMutableList()
                tempList.add(Subject("", ""))
                subjectsAdapter.submitList(tempList.toList())
            }
        }
    }

    private fun checkTheme(
        sharedPreferences: SharedPreferences?,
    ) {
        val theme = sharedPreferences?.getString(STRING_THEME, "light")
        Toast.makeText(context, theme, Toast.LENGTH_SHORT).show()
        if (theme != "light") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            (activity as AppCompatActivity).delegate.applyDayNight()
            sharedPreferences?.edit()?.putString(STRING_THEME, "light")?.apply()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            (activity as AppCompatActivity).delegate.applyDayNight()
            sharedPreferences.edit()?.putString(STRING_THEME, "dark")?.apply()
        }
    }

    private fun initTheme(
        sharedPreferences: SharedPreferences?,
    ) {
        val theme = sharedPreferences?.getString(STRING_THEME, "light")
        Toast.makeText(context, "theme "+ theme, Toast.LENGTH_SHORT).show()
        if (theme != "light") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            (activity as AppCompatActivity).delegate.applyDayNight()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            (activity as AppCompatActivity).delegate.applyDayNight()
        }
    }

    private fun deleteConfirmationDialog(subject: Subject, adapter: SubjectsAdapter): AlertDialog? {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Subject would be deleted permanently.")
                setMessage("Are you sure to delete the selected subject?")
                setPositiveButton("Yes",
                    DialogInterface.OnClickListener { _, _ ->
                        viewModel.deleteSubject(subject.id) { response ->
                            if (response)
                                Toast.makeText(context,
                                    "Subject has been deleted successfully.",
                                    Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(context,
                                    "Subject has NOT been deleted. Try Again!",
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
        const val KEY_REQUEST_STRING = "request"
        const val KEY_SUBJECT_BUNDLE = "subject"
        const val STRING_THEME = "currentTheme"

        fun getInstance(): Fragment {
            return SubjectsFragment()
        }
    }
}