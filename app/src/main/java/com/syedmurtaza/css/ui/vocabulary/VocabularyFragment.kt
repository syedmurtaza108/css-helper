package com.syedmurtaza.css.ui.vocabulary

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syedmurtaza.css.R
import com.syedmurtaza.css.databinding.FragmentVocabularyBinding
import com.syedmurtaza.css.models.Subject
import com.syedmurtaza.css.models.Vocabulary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class VocabularyFragment : Fragment() {

    private lateinit var binding: FragmentVocabularyBinding
    private var isEdit = false
    private val viewModel: VocabularyViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentVocabularyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vocabularyAdapter = VocabularyAdapter {
            val vocabularyBottomSheet = VocabularyBottomSheet()
            vocabularyBottomSheet.arguments = bundleOf(VocabularyBottomSheet.KEY_ARG_BUNDLE to it)
            vocabularyBottomSheet.show(parentFragmentManager, "")
            isEdit = true
            return@VocabularyAdapter true
        }

        binding.vocabList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = vocabularyAdapter
        }

        binding.searchView.apply {
            setOnSearchClickListener {
                binding.textView.isVisible = false
            }
            setOnCloseListener {
                binding.textView.isVisible = true
                return@setOnCloseListener false
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.searchVocabulary(query ?: "")
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
        }



        binding.swipRefresh.setOnRefreshListener {
            viewModel.reload()
        }

        setFragmentResultListener(KEY_REQUEST_STRING) { _, result: Bundle ->
            val vocabulary = result.getParcelable<Vocabulary>(KEY_VOCABULARY_BUNDLE)
            if (isEdit) {
                viewModel.updateVocabulary(vocabulary!!) {
                    if (it)
                        Toast.makeText(context,
                            "Vocab has been updated successfully.",
                            Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context,
                            "Vocab has NOT been updated. Try again!",
                            Toast.LENGTH_SHORT).show()
                }
                isEdit = false
            } else {
                viewModel.addVocabulary(vocabulary!!) {
                    if (it)
                        Toast.makeText(context,
                            "Vocab has been added successfully.",
                            Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context,
                            "Vocab has NOT been added. Try again!",
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
                deleteConfirmationDialog((viewHolder as VocabularyAdapter.VocabularyViewHolder).vocabulary!!,
                    vocabularyAdapter)?.show()
            }

        }).attachToRecyclerView(binding.vocabList)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.isRefresh.collect {
                binding.swipRefresh.isRefreshing = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.vocabulary.collect {
                val tempList = it.toMutableList()
                tempList.add(Vocabulary("", "","", "", ""))
                vocabularyAdapter.submitList(tempList.toList())
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.searchedVocabulary.collect {
                vocabularyAdapter.submitList(it)
            }
        }

    }

    private fun deleteConfirmationDialog(
        vocabulary: Vocabulary,
        adapter: VocabularyAdapter,
    ): AlertDialog? {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Vocab would be deleted permanently.")
                setMessage("Are you sure to delete the selected Vocab?")
                setPositiveButton("Yes",
                    DialogInterface.OnClickListener { _, _ ->
                        viewModel.deleteVocabulary(vocabulary.id) { response ->
                            if (response)
                                Toast.makeText(context,
                                    "Vocab has been deleted successfully.",
                                    Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(context,
                                    "Vocab has NOT been deleted. Try Again!",
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
        const val KEY_VOCABULARY_BUNDLE = "vocabulary"
        fun getInstance(): Fragment {
            return VocabularyFragment()
        }
    }
}